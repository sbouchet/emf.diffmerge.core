/**
 * <copyright>
 * 
 * Copyright (c) 2010-2012 Thales Global Services S.A.S.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Thales Global Services S.A.S. - initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.emf.diffmerge.ui.viewers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.diffmerge.api.IComparison;
import org.eclipse.emf.diffmerge.api.IMatch;
import org.eclipse.emf.diffmerge.api.Role;
import org.eclipse.emf.diffmerge.api.diff.IAttributeValuePresence;
import org.eclipse.emf.diffmerge.api.diff.IDifference;
import org.eclipse.emf.diffmerge.api.diff.IReferenceValuePresence;
import org.eclipse.emf.diffmerge.api.diff.IValuePresence;
import org.eclipse.emf.diffmerge.ui.EMFDiffMergeUIPlugin;
import org.eclipse.emf.diffmerge.ui.EMFDiffMergeUIPlugin.DifferenceColorKind;
import org.eclipse.emf.diffmerge.ui.EMFDiffMergeUIPlugin.ImageID;
import org.eclipse.emf.diffmerge.ui.Messages;
import org.eclipse.emf.diffmerge.ui.diffuidata.MatchAndFeature;
import org.eclipse.emf.diffmerge.ui.util.DelegatingLabelProvider;
import org.eclipse.emf.diffmerge.ui.util.DifferenceKind;
import org.eclipse.emf.diffmerge.ui.util.UIUtil;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;


/**
 * A viewer which provides a representation of the values of a feature on a match.
 * Input: ValuesViewer.ValuesInput ; Elements: [IValuePresence (if !showAllValues)] or
 * [[Object (if feature instanceof EAttribute)] or
 * [IMatch (if feature instanceof EReference)] (if showAllValues)].
 * @author Olivier Constant
 */
public class ValuesViewer extends TableViewer {
  
  /**
   * A simple structure for defining inputs for this viewer.
   */
  public static class ValuesInput {
    /** The non-null comparison context */
    private final EMFDiffNode _context;
    /** The non-null specific part */
    private final MatchAndFeature _matchAndFeature;
    /**
     * Constructor
     * @param context_p a non-null object
     * @param matchAndFeature_p a non-null object
     */
    public ValuesInput(EMFDiffNode context_p,
        MatchAndFeature matchAndFeature_p) {
      _context = context_p;
      _matchAndFeature = matchAndFeature_p;
    }
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object_p) {
      boolean result = false;
      if (object_p instanceof ValuesInput) {
        ValuesInput peer = (ValuesInput)object_p;
        result = _context == peer.getContext() &&
          _matchAndFeature.getFeature() == peer.getMatchAndFeature().getFeature() &&
          _matchAndFeature.getMatch().equals(peer.getMatchAndFeature().getMatch());
      }
      return result;
    }
    /**
     * Return the comparison context
     * @return a non-null object
     */
    public EMFDiffNode getContext() {
      return _context;
    }
    /**
     * Return the match and feature
     * @return a non-null object
     */
    public MatchAndFeature getMatchAndFeature() {
      return _matchAndFeature;
    }
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      return _context.hashCode() + _matchAndFeature.getMatch().hashCode() +
        _matchAndFeature.getFeature().hashCode();
    }
  }
  
  
  /** Whether the side of the viewer is left or right */
  private final boolean _sideIsLeft;
  
  /** Whether all values must be shown, including those not related to a difference */
  private boolean _showAllValues;
  
  
  /**
   * Constructor
   * @param parent_p a non-null composite
   * @param sideIsLeft_p whether the side is left or right
   */
  public ValuesViewer(Composite parent_p, boolean sideIsLeft_p) {
    this(parent_p, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, sideIsLeft_p);
  }
  
  /**
   * Constructor
   * @param parent_p a non-null composite
   * @param style_p a style for the tree
   * @param sideIsLeft_p whether the side is left or right
   */
  public ValuesViewer(Composite parent_p, int style_p, boolean sideIsLeft_p) {
    super(parent_p, style_p);
    setContentProvider(new ContentProvider());
    setLabelProvider(new LabelProvider());
    _sideIsLeft = sideIsLeft_p;
    _showAllValues = false;
    getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
  }
  
  /**
   * Return the model element that corresponds to the given viewer value, if applicable
   * @param viewerElement_p a potentially null object
   * @return a potentially null element
   */
  public EObject getElementForValue(Object viewerValueElement_p) {
    EObject result;
    if (viewerValueElement_p instanceof IReferenceValuePresence)
      result = (EObject)getValueToRepresent((IValuePresence)viewerValueElement_p);
    else if (viewerValueElement_p instanceof IMatch)
      result = ((IMatch)viewerValueElement_p).get(getSideRole());
    else if (viewerValueElement_p instanceof EObject &&
        !(viewerValueElement_p instanceof IDifference))
      result = (EObject)viewerValueElement_p;
    else
      result = null;
    return result;
  }
  
  /**
   * @see org.eclipse.jface.viewers.ContentViewer#getInput()
   */
  @Override
  public ValuesInput getInput() {
    return (ValuesInput)super.getInput();
  }
  
  /**
   * Return the resource manager for this viewer
   * @return a resource manager which is non-null iff input is not null
   */
  protected ComparisonResourceManager getResourceManager() {
    return getInput() == null? null: getInput().getContext().getResourceManager();
  }
  
  /**
   * Return the role that corresponds to the values being represented
   * @return a role which is null if and only if the input is null
   */
  protected Role getSideRole() {
    return getInput() == null? null:
      getInput().getContext().getRoleForSide(isLeftSide());
  }
  
  /**
   * @see org.eclipse.jface.viewers.StructuredViewer#getSelection()
   */
  @Override
  public IStructuredSelection getSelection() {
    return (IStructuredSelection)super.getSelection();
  }
  
  /**
   * Return the value object of the given value presence that should be represented
   * @param presence_p a non-null value presence
   * @return a non-null object
   */
  protected Object getValueToRepresent(IValuePresence presence_p) {
    Object result;
    if (presence_p.getFeature() instanceof EAttribute)
      result = presence_p.getValue();
    else
      if (isOwnership(getInput().getMatchAndFeature()))
        result = ((IReferenceValuePresence)presence_p).getElementMatch().get(presence_p.getPresenceRole());
      else
        result = ((IReferenceValuePresence)presence_p).getValue().get(presence_p.getPresenceRole());
    return result;
  }
  
  /**
   * Return whether the given input object represents a containment
   * @param object_p a potentially null object
   */
  protected boolean isContainment(Object object_p) {
    boolean result = false;
    if (object_p instanceof MatchAndFeature) {
      EStructuralFeature feature = ((MatchAndFeature)object_p).getFeature();
      result = feature instanceof EReference && ((EReference)feature).isContainment();
    }
    return result;
  }
  
  /**
   * Return whether the side of this viewer is left or right
   * @return a non-null role
   */
  public boolean isLeftSide() {
    return _sideIsLeft;
  }
  
  /**
   * Return whether the given input object represents the virtual ownership feature
   * @param object_p a potentially null object
   */
  protected boolean isOwnership(Object object_p) {
    boolean result = false;
    Object object = object_p;
    if (object instanceof ValuesInput)
      object = ((ValuesInput)object).getMatchAndFeature();
    if (object instanceof MatchAndFeature) {
      EStructuralFeature feature = ((MatchAndFeature)object).getFeature();
      result = EMFDiffMergeUIPlugin.getDefault().getOwnershipFeature().equals(feature);
    }
    return result;
  }
  
  /**
   * Return whether all values must be shown, including those unrelated to differences
   */
  public boolean mustShowAllValues() {
    return _showAllValues;
  }
  
  /**
   * Set whether all values must be shown, including those unrelated to differences
   * @param show_p whether all values must be shown
   */
  public void setShowAllValues(boolean show_p) {
    if (show_p != mustShowAllValues()) {
      _showAllValues = show_p;
      refresh(false);
    }
  }
  
  /**
   * Return whether the given object must be represented as a difference
   * @param element_p a potentially null object
   */
  protected boolean showAsDifference(Object element_p) {
    return element_p instanceof IValuePresence &&
      !((IValuePresence)element_p).isMerged() &&
      !getInput().getContext().shouldBeIgnored((IDifference)element_p);
  }
  
  
  /**
   * The content provider for this viewer.
   */
  protected class ContentProvider implements IStructuredContentProvider {
    
    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement_p) {
      MatchAndFeature input = ((ValuesInput)inputElement_p).getMatchAndFeature();
      Collection<Object> result = new ArrayList<Object>();
      if (isOwnership(input)) {
        // Ownership
        IReferenceValuePresence ownership = input.getMatch().getOwnershipDifference(getSideRole());
        if (ownership != null)
          result.add(ownership);
      } else {
        // Order
        IValuePresence orderDifference = input.getMatch().getOrderDifference(
            input.getFeature(), getSideRole());
        if (orderDifference != null)
          result.add(orderDifference);
        // Only show values if no containment
        if (!isContainment(input)) {
          if (mustShowAllValues()) {
            // All values
            if (input.getFeature() instanceof EAttribute) {
              EAttribute attribute = (EAttribute)input.getFeature();
              IComparison comparison = input.getMatch().getMapping().getComparison();
              IMatch match = input.getMatch();
              EObject source = match.get(getSideRole());
              if (source != null) {
                List<Object> values = comparison.getScope(getSideRole()).get(source, attribute);
                for (Object value : values) {
                  IAttributeValuePresence presence =
                    match.getAttributeValueDifference(attribute, value);
                  if (presence != null)
                    result.add(presence);
                  else
                    result.add(value);
                }
              }
            } else {
              EReference reference = (EReference)input.getFeature();
              IComparison comparison = input.getMatch().getMapping().getComparison();
              IMatch match = input.getMatch();
              EObject source = match.get(getSideRole());
              if (source != null) {
                List<EObject> values = comparison.getScope(getSideRole()).get(source, reference);
                for (EObject value : values) {
                  IMatch valueMatch = comparison.getMapping().getMatchFor(value, getSideRole());
                  if (valueMatch != null) {
                    IReferenceValuePresence presence =
                      match.getReferenceValueDifference(reference, valueMatch);
                    if (presence != null)
                      result.add(presence);
                    else
                      result.add(valueMatch);
                  }
                }
              }
            }
          } else {
            // Only differences
            Collection<? extends IValuePresence> bothSides;
            if (input.getFeature() instanceof EAttribute)
              bothSides = input.getMatch().getAttributeDifferences((EAttribute)input.getFeature());
            else
              bothSides = input.getMatch().getReferenceDifferences((EReference)input.getFeature());
            for (IValuePresence presence : bothSides) {
              if (!presence.isOrder() && presence.getPresenceRole() == getSideRole() &&
                  presence.getMergeDestination() != getSideRole() ||
                  !presence.isOrder() && presence.getPresenceRole() == getSideRole().opposite() &&
                  presence.getMergeDestination() == getSideRole())
                result.add(presence);
            }
          }
        }
      }
      return result.toArray();
    }
    
    /**
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
      // Nothing needed
    }
    
    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer_p, Object oldInput_p, Object newInput_p) {
      // Nothing needed
    }
  }
  
  
  /**
   * The label provider for this viewer.
   */
  protected class LabelProvider extends DelegatingLabelProvider {
    
    /**
     * Adapt the given label that describes the cross-reference of the given value
     * @param initialLabel_p a potentially null string
     * @param value_p a non-null element
     * @return a potentially null string
     */
    private String formatCrossReferenceValue(String initialLabel_p, EObject value_p) {
      StringBuilder builder = new StringBuilder();
      if (initialLabel_p != null)
        builder.append(initialLabel_p);
      EObject container = value_p.eContainer();
      String containerLabel = container == null? null:
        getDelegate().getText(container);
      if (containerLabel != null) {
        builder.append(' ');
        builder.append(String.format(Messages.ValuesViewer_ContainerLabel, containerLabel));
      }
      return builder.toString();
    }
    
    /**
     * Adapt the given label of the owner of the given reference value presence so that it conveniently
     * describes a containment
     * @param ownerLabel_p a potentially null string
     * @param presence_p a non-null reference value presence
     * @return a potentially null string
     */
    private String formatOwnershipValue(String ownerLabel_p, IReferenceValuePresence presence_p) {
      StringBuilder builder = new StringBuilder();
      if (ownerLabel_p != null)
        builder.append(ownerLabel_p);
      EReference containment = presence_p.getFeature();
      if (containment != null) {
        builder.append(' ');
        builder.append(String.format(Messages.ValuesViewer_FeatureLabel, containment.getName()));
      }
      return builder.toString();
    }
    
    /**
     * @see org.eclipse.emf.diffmerge.ui.util.DelegatingLabelProvider#getFont(java.lang.Object)
     */
    @Override
    public Font getFont(Object element_p) {
      Font result = getControl().getFont();
      if (showAsDifference(element_p))
        result = UIUtil.getBold(result);
      return result;
    }
    
    /**
     * @see org.eclipse.emf.diffmerge.ui.util.DelegatingLabelProvider#getForeground(java.lang.Object)
     */
    @Override
    public Color getForeground(Object element_p) {
      DifferenceColorKind result;
      if (showAsDifference(element_p)) {
        result = (getSideRole() == getInput().getContext().getDrivingRole())?
            DifferenceColorKind.LEFT: DifferenceColorKind.RIGHT;
      } else {
        result = DifferenceColorKind.DEFAULT;
      }
      return getInput().getContext().getDifferenceColor(result);
    }
    
    /**
     * @see org.eclipse.emf.diffmerge.ui.util.DelegatingLabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage(Object element_p) {
      Image result;
      if (element_p instanceof IValuePresence) {
        IValuePresence presence = (IValuePresence)element_p;
        if (presence.isOrder()) {
          result = EMFDiffMergeUIPlugin.getDefault().getImage(ImageID.SORT);
        } else {
          Object toRepresent = getValueToRepresent(presence);
          result = getDelegate().getImage(toRepresent);
        }
        if (getInput().getContext().usesCustomIcons()) {
          DifferenceKind kind;
          if (isOwnership(getInput().getMatchAndFeature()) &&
              getInput().getContext().getReferenceRole() == null) {
            kind = DifferenceKind.MODIFIED;
          } else {
            kind = getInput().getContext().getDifferenceKind(presence);
          }
          result = getResourceManager().adaptImage(result, kind);
        }
      } else if (element_p instanceof IMatch) {
        result = getDelegate().getImage(((IMatch)element_p).get(getSideRole()));
      } else {
        result = getDelegate().getImage(element_p);
      }
      return result;
    }
    
    /**
     * @see org.eclipse.emf.diffmerge.ui.util.DelegatingLabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object element_p) {
      String result;
      if (element_p instanceof IValuePresence) {
        IValuePresence presence = (IValuePresence)element_p;
        if (presence.isOrder()) {
          result = Messages.ValuesViewer_OrderLabel;
        } else {
          Object toRepresent = getValueToRepresent(presence);
          result = getDelegate().getText(toRepresent);
          if (isOwnership(getInput()))
            result = formatOwnershipValue(result, (IReferenceValuePresence)presence);
          else if (toRepresent instanceof EObject)
            result = formatCrossReferenceValue(result, (EObject)toRepresent);
        }
        if (getInput().getContext().usesCustomLabels()) {
          DifferenceKind kind = getInput().getContext().getDifferenceKind(presence);
          String prefix = EMFDiffMergeUIPlugin.getDefault().getDifferencePrefix(kind);
          result = prefix + result;
        }
      } else if (element_p instanceof IMatch) {
        result = getDelegate().getText(((IMatch)element_p).get(getSideRole()));
      } else {
        result = getDelegate().getText(element_p);
      }
      return result;
    }
  }
  
}
