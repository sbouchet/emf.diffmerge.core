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

import static org.eclipse.emf.diffmerge.ui.util.UIUtil.createComposite;
import static org.eclipse.emf.diffmerge.ui.util.UIUtil.createToolBar;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.common.util.Logger;
import org.eclipse.emf.diffmerge.api.IComparison;
import org.eclipse.emf.diffmerge.api.IMatch;
import org.eclipse.emf.diffmerge.api.Role;
import org.eclipse.emf.diffmerge.api.diff.IDifference;
import org.eclipse.emf.diffmerge.api.diff.IPresenceDifference;
import org.eclipse.emf.diffmerge.api.diff.IReferenceValuePresence;
import org.eclipse.emf.diffmerge.api.diff.IValuePresence;
import org.eclipse.emf.diffmerge.diffdata.EComparison;
import org.eclipse.emf.diffmerge.diffdata.EElementRelativePresence;
import org.eclipse.emf.diffmerge.diffdata.EMatch;
import org.eclipse.emf.diffmerge.diffdata.EMergeableDifference;
import org.eclipse.emf.diffmerge.diffdata.EValuePresence;
import org.eclipse.emf.diffmerge.ui.EMFDiffMergeUIPlugin;
import org.eclipse.emf.diffmerge.ui.EMFDiffMergeUIPlugin.ImageID;
import org.eclipse.emf.diffmerge.ui.Messages;
import org.eclipse.emf.diffmerge.ui.diffuidata.ComparisonSelection;
import org.eclipse.emf.diffmerge.ui.diffuidata.MatchAndFeature;
import org.eclipse.emf.diffmerge.ui.diffuidata.impl.ComparisonSelectionImpl;
import org.eclipse.emf.diffmerge.ui.diffuidata.impl.MatchAndFeatureImpl;
import org.eclipse.emf.diffmerge.ui.log.CompareLogEvent;
import org.eclipse.emf.diffmerge.ui.log.MergeLogEvent;
import org.eclipse.emf.diffmerge.ui.util.DelegatingLabelProvider;
import org.eclipse.emf.diffmerge.ui.util.DifferenceKind;
import org.eclipse.emf.diffmerge.ui.util.InconsistencyDialog;
import org.eclipse.emf.diffmerge.ui.util.MiscUtil;
import org.eclipse.emf.diffmerge.ui.util.UIUtil;
import org.eclipse.emf.diffmerge.ui.viewers.EMFDiffNode.UserDifferenceKind;
import org.eclipse.emf.diffmerge.ui.viewers.FeaturesViewer.FeaturesInput;
import org.eclipse.emf.diffmerge.ui.viewers.IgnoreChoicesDialog.IgnoreChoiceData;
import org.eclipse.emf.diffmerge.ui.viewers.MergeChoicesDialog.MergeChoiceDialogData;
import org.eclipse.emf.diffmerge.ui.viewers.MergeImpactViewer.ImpactInput;
import org.eclipse.emf.diffmerge.ui.viewers.ValuesViewer.ValuesInput;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;


/**
 * A Viewer for comparisons which is composed of six sub-viewers that show the scopes being
 * compared, a synthesis of the differences, features of the selected element, and the contents
 * of the selected feature in each scope.
 * Input: EMFDiffNode ; Elements: IMatch | IDifference.
 * @author Olivier Constant
 */
public class ComparisonViewer extends AbstractComparisonViewer {
  
  /** The name of the "filtering state" property */
  protected static final String PROPERTY_FILTERING = "PROPERTY_FILTERING"; //$NON-NLS-1$
  
  /** The name of the "difference numbers" property */
  protected static final String PROPERTY_DIFFERENCE_NUMBERS = "PROPERTY_DIFFERENCE_NUMBERS"; //$NON-NLS-1$
  
  /** The synthesis model tree viewer */
  protected EnhancedComparisonTreeViewer _synthesisModelTreeViewer;
  
  /** The left model tree viewer */
  protected EnhancedComparisonSideViewer _leftModelTreeViewer;
  
  /** The right model tree viewer */
  protected EnhancedComparisonSideViewer _rightModelTreeViewer;

  /** The features viewer */
  protected FeaturesViewer _featuresViewer;

  /** The left values viewer */
  protected ValuesViewer _leftValuesViewer;
  
  /** The right values viewer */
  protected ValuesViewer _rightValuesViewer;
  
  /** The tool bar above the features viewer */
  protected ToolBar _featuresToolbar;
  
  /** The tool bar above the left values viewer */
  protected ToolBar _leftValuesToolbar;
  
  /** The tool bar above the right values viewer */
  protected ToolBar _rightValuesToolbar;
  
  /** The tool items */
  protected ToolItem _nextItem, _previousItem, _toLeftItem, _toRightItem,
    _deleteLeftItem, _deleteRightItem, _ignoreLeftItem, _ignoreRightItem;
  
  /** The last selection directly made by the user */
  private ComparisonSelection _lastUserSelection;
  
  /** A filter for unchanged elements */
  protected ViewerFilter _unchangedElementsFilter;
  
  /** A filter for move origins */
  protected ViewerFilter _moveOriginsFilter;
  
  /** A sorter for the synthesis view */
  protected ViewerSorter _synthesisSorter;
  
  /** Whether the left and right trees are synchronized with the synthesis tree */
  protected boolean _isLeftRightSynced;
  
  
  /**
   * Constructor
   * @param parent_p a non-null composite
   */
  public ComparisonViewer(Composite parent_p) {
    this(parent_p, null);
  }
  
  /**
   * Constructor
   * @param parent_p a non-null composite
   * @param actionBars_p optional action bars
   */
  public ComparisonViewer(Composite parent_p, IActionBars actionBars_p) {
    super(parent_p, actionBars_p);
    _isLeftRightSynced = true;
    _lastUserSelection = null;
  }
  
  /**
   * Add differences to merge on the given match to the given list according
   * to the given criteria
   * @param toMerge_p a non-null, modifiable list
   * @param match_p a non-null match
   * @param destination_p a non-null role which is TARGET or REFEREBCE
   * @param incrementalMode_p whether optional deletions must be skipped
   * @return a non-null, potentially empty, unmodifiable list
   */
  protected void addDifferencesToMerge(List<IDifference> toMerge_p, IMatch match_p,
      Role destination_p, boolean incrementalMode_p) {
    for (IDifference difference : match_p.getAllDifferences()) {
      if (!getInput().shouldBeIgnored(difference)) {
        if (!incrementalMode_p || difference instanceof IPresenceDifference &&
            ((IPresenceDifference)difference).getPresenceRole() != destination_p)
          toMerge_p.add(difference);
      }
    }
  }
  
  /**
   * Add differences to merge on the given match and its children to the given list according
   * to the given criteria
   * @param toMerge_p a non-null, modifiable list
   * @param match_p a non-null match
   * @param destination_p a non-null role which is TARGET or REFEREBCE
   * @param incrementalMode_p whether optional deletions must be skipped
   * @return a non-null, potentially empty, unmodifiable list
   */
  protected void addDifferencesToMergeRec(List<IDifference> toMerge_p, IMatch match_p,
      Role destination_p, boolean incrementalMode_p) {
    addDifferencesToMerge(toMerge_p, match_p, destination_p, incrementalMode_p);
    for (IMatch child : getInput().getChildrenForMerge(match_p)) {
      addDifferencesToMergeRec(toMerge_p, child, destination_p, incrementalMode_p);
    }
  }
  
  /**
   * Return whether the given situation allows adding to the left
   * @param originKind_p a non-null kind
   */
  protected boolean canAddToTheLeft(DifferenceKind originKind_p) {
    final Collection<DifferenceKind> allowed = Arrays.asList(
        DifferenceKind.CONFLICT,
        DifferenceKind.MODIFIED,
        DifferenceKind.FROM_BOTH,
        DifferenceKind.FROM_RIGHT,
        DifferenceKind.FROM_RIGHT_ADD,
        DifferenceKind.FROM_LEFT_DEL);
    return allowed.contains(originKind_p);
  }
  
  /**
   * Return whether the given situation allows adding to the right
   * @param originKind_p a non-null kind
   */
  protected boolean canAddToTheRight(DifferenceKind originKind_p) {
    final Collection<DifferenceKind> allowed = Arrays.asList(
        DifferenceKind.CONFLICT,
        DifferenceKind.MODIFIED,
        DifferenceKind.FROM_BOTH,
        DifferenceKind.FROM_LEFT,
        DifferenceKind.FROM_LEFT_ADD,
        DifferenceKind.FROM_RIGHT_DEL);
    return allowed.contains(originKind_p);
  }
  
  /**
   * Set the given difference counting feature to the given value
   * @param kind_p the kind of difference
   * @param newValue_p the new boolean value specifying whether to count it or not
   */
  protected void changeCounting(final UserDifferenceKind kind_p, final boolean newValue_p) {
    BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
      /**
       * @see java.lang.Runnable#run()
       */
      public void run() {
        getInput().setCount(kind_p, newValue_p);
        firePropertyChangeEvent(PROPERTY_DIFFERENCE_NUMBERS, null);
        firePropertyChangeEvent(PROPERTY_FILTERING, Boolean.valueOf(getInput().isFiltering()));
      }
    });
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.ui.viewers.AbstractComparisonViewer#createControls(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected Composite createControls(Composite parent_p) {
    addPropertyChangeListener(new IPropertyChangeListener() {
      /**
       * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
       */
      public void propertyChange(PropertyChangeEvent event_p) {
        if (PROPERTY_DIFFERENCE_NUMBERS.equals(event_p.getProperty())) {
          getInput().updateDifferenceNumbers();
          refresh();
        }
      }
    });
    SashForm result = new SashForm(parent_p, SWT.VERTICAL);
    // Upper part
    SashForm upperPart = new SashForm(result, SWT.HORIZONTAL);
    _synthesisModelTreeViewer = new EnhancedComparisonTreeViewer(upperPart);
    _synthesisSorter = new ViewerSorter();
    _unchangedElementsFilter = new ViewerFilter() {
      /**
       * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
       */
      @Override
      public boolean select(Viewer viewer_p, Object parentElement_p, Object element_p) {
        EMatch match = (EMatch)element_p;
        return getInput().getDifferenceNumber(match) > 0;
      }
    };
    _moveOriginsFilter = new ViewerFilter() {
      /**
       * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
       */
      @Override
      public boolean select(Viewer viewer_p, Object parentElement_p, Object element_p) {
        TreePath path;
        if (parentElement_p instanceof TreePath)
          path = ((TreePath)parentElement_p).createChildPath(element_p);
        else
          path = new TreePath(new Object[] {element_p});
        return !getInput().isMoveOrigin(path);
      }
    };
    _synthesisModelTreeViewer.getComparisonTreeViewer().addFilter(_unchangedElementsFilter);
    addPropertyChangeListener(new IPropertyChangeListener() {
      /**
       * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
       */
      public void propertyChange(PropertyChangeEvent event_p) {
        if (PROPERTY_FILTERING.equals(event_p.getProperty())) {
          Boolean filtered = (Boolean)event_p.getNewValue();
          if (filtered != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(_synthesisModelTreeViewer.getDefaultHeaderText());
            if (filtered.booleanValue())
              builder.append(Messages.ComparisonViewer_Filtered);
            _synthesisModelTreeViewer.getHeader().setText(builder.toString());
          }
        }
      }
    });
    _leftModelTreeViewer = new EnhancedComparisonSideViewer(upperPart, true);
    _rightModelTreeViewer = new EnhancedComparisonSideViewer(upperPart, false);
    // Lower part
    SashForm lowerPart = new SashForm(result, SWT.HORIZONTAL);
    // Features section
    Composite featuresWrapper = createComposite(lowerPart);
    Composite featuresHeader = new Composite(featuresWrapper, SWT.NONE);
    _featuresViewer = new FeaturesViewer(featuresWrapper);
    // Features header
    featuresHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    GridLayout layout = new GridLayout(2, false);
    layout.marginHeight = 0;
    featuresHeader.setLayout(layout);
    Label label = new Label(featuresHeader, SWT.NONE);
    label.setFont(UIUtil.getBold(label.getFont()));
    label.setText(Messages.ComparisonViewer_Details);
    label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    _featuresToolbar = createToolBar(featuresHeader);
    // Values section
    _leftValuesViewer = createValuesSection(lowerPart, true);
    _rightValuesViewer = createValuesSection(lowerPart, false);
    setupSashes(upperPart, lowerPart);
    result.setWeights(new int[] {5, 2});
    setupSynchronizationListeners();
    setupToolbars();
    return result;
  }
  
  /**
   * Create the "collapse all" tool in the given tool bar
   * @param toolbar_p a non-null tool bar
   */
  protected void createCollapseTool(ToolBar toolbar_p) {
    ToolItem collapseTool = new ToolItem(toolbar_p, SWT.PUSH);
    collapseTool.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(
        EMFDiffMergeUIPlugin.ImageID.COLLAPSEALL));
    collapseTool.setToolTipText(Messages.ComparisonViewer_CollapseTooltip);
    collapseTool.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent event_p) {
        BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
          /**
           * @see java.lang.Runnable#run()
           */
          public void run() {
            _synthesisModelTreeViewer.getComparisonTreeViewer().collapseAll();
          }
        });
      }
    });
  }
  
  /**
   * Create the "expand all" tool in the given tool bar
   * @param toolbar_p a non-null tool bar
   */
  protected void createExpandTool(ToolBar toolbar_p) {
    ToolItem expandTool = new ToolItem(toolbar_p, SWT.PUSH);
    expandTool.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(
        EMFDiffMergeUIPlugin.ImageID.EXPANDALL));
    expandTool.setToolTipText(Messages.ComparisonViewer_ExpandTooltip);
    expandTool.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent event_p) {
        BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
          /**
           * @see java.lang.Runnable#run()
           */
          public void run() {
            _synthesisModelTreeViewer.getComparisonTreeViewer().expandAll();
          }
        });
      }
    });
  }
  
  /**
   * Create the "inconsistency" tool in the given tool bar, if relevant
   * @param toolbar_p a non-null tool bar
   */
  protected void createInconsistencyTool(ToolBar toolbar_p) {
    final ToolItem expandTool = new ToolItem(toolbar_p, SWT.PUSH);
    expandTool.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(
        EMFDiffMergeUIPlugin.ImageID.WARNING));
    expandTool.setToolTipText(Messages.ComparisonViewer_InconsistencyTooltip);
    expandTool.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent event_p) {
        final Shell shell = getShell();
        final EComparison comparison = getComparison();
        if (shell != null && comparison != null) {
          shell.getDisplay().syncExec(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
              InconsistencyDialog dialog = new InconsistencyDialog(shell, comparison);
              dialog.open();
            }
          });
        }
      }
    });
    expandTool.setEnabled(false);
    addPropertyChangeListener(new IPropertyChangeListener() {
      /**
       * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
       */
      public void propertyChange(PropertyChangeEvent event_p) {
        if (PROPERTY_CURRENT_INPUT.equals(event_p.getProperty())) {
          IComparison comparison = getComparison();
          expandTool.setEnabled(comparison != null && !comparison.isConsistent());
        }
      }
    });
  }
  
  /**
   * Create the "sort" tool in the given tool bar
   * @param toolbar_p a non-null tool bar
   */
  protected void createSortTool(ToolBar toolbar_p) {
    final ToolItem sortItem = new ToolItem(toolbar_p, SWT.CHECK);
    sortItem.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(
        EMFDiffMergeUIPlugin.ImageID.SORT));
    sortItem.setToolTipText(Messages.ComparisonViewer_SortTooltip);
    sortItem.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent event_p) {
        if (sortItem.getSelection())
          _synthesisModelTreeViewer.getComparisonTreeViewer().setSorter(_synthesisSorter);
        else
          _synthesisModelTreeViewer.getComparisonTreeViewer().setSorter(null);
      }
    });
  }
  
  /**
   * Create the "sync" tool in the given tool bar
   * @param toolbar_p a non-null tool bar
   */
  protected void createSyncTool(ToolBar toolbar_p) {
    final ToolItem syncTool = new ToolItem(toolbar_p, SWT.CHECK);
    syncTool.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(
        EMFDiffMergeUIPlugin.ImageID.SYNCED));
    syncTool.setToolTipText(Messages.ComparisonViewer_LinkViewsTooltip);
    syncTool.setSelection(_isLeftRightSynced);
    syncTool.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent event_p) {
        boolean synced = syncTool.getSelection();
        _isLeftRightSynced = synced;
        if (synced) {
          BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
              ISelection selection = _synthesisModelTreeViewer.getSelection();
              _leftModelTreeViewer.setSelection(selection, true);
              _rightModelTreeViewer.setSelection(selection, true);
            }
          });
        }
      }
    });
  }
  
  /**
   * Create and return the values section on the given side
   * @param parent_p a non-null composite
   * @param left_p whether the side is left or right
   * @return a non-null viewer
   */
  protected ValuesViewer createValuesSection(Composite parent_p, boolean left_p) {
    // Main controls
    Composite sectionWrapper = createComposite(parent_p);
    Composite header = new Composite(sectionWrapper, SWT.NONE);
    header.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    ValuesViewer result = new ValuesViewer(sectionWrapper, left_p);
    // Header controls
    GridLayout layout = new GridLayout(2, false);
    layout.marginHeight = 0;
    header.setLayout(layout);
    if (left_p) {
      // Send tool bar to the right
      Label space = new Label(header, SWT.NONE);
      space.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    }
    ToolBar toolbar = createToolBar(header);
    if (left_p) _leftValuesToolbar = toolbar; else _rightValuesToolbar = toolbar;
    return result;
  }
  
  /**
   * Ignore the current selection
   * @param onLeft_p whether ignore occurs on the left
   */
  protected void ignore(boolean onLeft_p) {
    final ComparisonSelection selection = getSelection();
    if (selection == null) return;
    boolean coverChildren = getInput().isDefaultCoverChildren();
    boolean sideExclusive = false;
    List<EMatch> selectedMatches = selection.getSelectedMatches();
    if (selectedMatches.isEmpty()) {
      List<EMatch> treePath = selection.getSelectedTreePath();
      if (!treePath.isEmpty())
        selectedMatches = Collections.singletonList(treePath.get(treePath.size()-1));
    }
    boolean basedOnMatches = !selectedMatches.isEmpty();
    boolean requiresChoices = false;
    if (basedOnMatches) {
      for (EMatch selectedMatch : selectedMatches) {
        if (getInput().hasChildrenForMerge(selectedMatch)) {
          requiresChoices = true;
          break;
        }
      }
    }
    if (requiresChoices) {
      // Group of differences
      IgnoreChoiceData choice = new IgnoreChoiceData(coverChildren, false);
      IgnoreChoicesDialog choicesDialog =
        new IgnoreChoicesDialog(getShell(), Messages.ComparisonViewer_IgnoreCommandName, choice);
      int answer = choicesDialog.open();
      if (0 != answer) return;
      coverChildren = choice.getCoverChildren();
      getInput().setDefaultCoverChildren(choice.getCoverChildren());
      sideExclusive = choice.getSideExclusive();
    }
    final Collection<IDifference> toIgnore = basedOnMatches?
        getDifferencesToMerge(selectedMatches, getInput().getRoleForSide(onLeft_p), coverChildren, sideExclusive):
          getInput().getNonIgnoredDifferences(selection.asDifferencesToMerge());
    if (!toIgnore.isEmpty()) {
      executeOnModel(new Runnable() {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
          for (IDifference diff : toIgnore) {
            if (diff instanceof EElementRelativePresence) {
              EElementRelativePresence presence = (EElementRelativePresence)diff;
              getUIComparison().getDifferencesToIgnore().add(presence);
              // Also on symmetrical if any
              if (diff instanceof EValuePresence) {
                IValuePresence symmetrical = ((EValuePresence)diff).getSymmetrical();
                if (symmetrical instanceof EMergeableDifference)
                  getUIComparison().getDifferencesToIgnore().add(
                      (EMergeableDifference)symmetrical);
              }
            }
          }
          getUIComparison().setLastActionSelection(selection);
        }
      });
      firePropertyChangeEvent(CompareEditorInput.DIRTY_STATE, new Boolean(true));
      firePropertyChangeEvent(PROPERTY_DIFFERENCE_NUMBERS, null);
    }
  }
  
  /**
   * Return the differences to merge from a given list of selected matches and the given
   * criteria
   * @param selectedMatches_p a non-null list
   * @param coverChildren_p whether children of the matches must be covered
   * @param incrementalMode_p whether optional deletions must be skipped
   * @return a non-null, potentially empty, unmodifiable list
   */
  protected List<IDifference> getDifferencesToMerge(final List<EMatch> selectedMatches_p,
      final Role destination_p, final boolean coverChildren_p, final boolean incrementalMode_p) {
    final List<IDifference> result = new ArrayList<IDifference>();
    IProgressService progress = PlatformUI.getWorkbench().getProgressService();
    try {
      progress.busyCursorWhile(new IRunnableWithProgress() {
        /**
         * @see org.eclipse.jface.operation.IRunnableWithProgress#run(IProgressMonitor)
         */
        public void run(final IProgressMonitor monitor_p)
        throws InvocationTargetException, InterruptedException {
          for (EMatch selectedMatch : selectedMatches_p) {
            if (coverChildren_p)
              addDifferencesToMergeRec(result, selectedMatch, destination_p, incrementalMode_p);
            else
              addDifferencesToMerge(result, selectedMatch, destination_p, incrementalMode_p);
          }
        }
      });
    } catch (Exception e) {
      // Proceed
    }
    return Collections.unmodifiableList(result);
  }
  
  /**
   * Return the driving role for this viewer
   * @return a role which is assumed non-null after setInput(Object) has been invoked
   */
  public Role getDrivingRole() {
    return getInput() == null? null: getInput().getDrivingRole();
  }
  
  /**
   * Return the inner viewer for the features of elements with differences
   * @return a viewer which is non-null if this viewer has been properly initialized
   */
  public FeaturesViewer getFeaturesViewer() {
    return _featuresViewer;
  }
  
  /**
   * Return the logger for diff/merge events
   * @return a non-null logger
   */
  protected Logger getLogger() {
    return EMFDiffMergeUIPlugin.getDefault().getDiffMergeLogger();
  }
  
  /**
   * Return the inner viewer for the model from the given side
   * @param left_p whether the side is left or right
   * @return a viewer which is non-null if this viewer has been properly initialized
   */
  public EnhancedComparisonSideViewer getModelScopeViewer(boolean left_p) {
    EnhancedComparisonSideViewer result = left_p? _leftModelTreeViewer:
      _rightModelTreeViewer;
    return result;
  }
  
  /**
   * @see org.eclipse.jface.viewers.Viewer#getSelection()
   */
  @Override
  public ComparisonSelection getSelection() {
    return _lastUserSelection;
  }
  
  /**
   * Return the inner viewer for the comparison tree
   * @return a viewer which is non-null if this viewer has been properly initialized
   */
  public EnhancedComparisonTreeViewer getSynthesisViewer() {
    return _synthesisModelTreeViewer;
  }
  
  /**
   * Return the inner viewer for differences on values for the given side
   * @param left_p whether the side is left or right
   * @return a viewer which is non-null if this viewer has been properly initialized
   */
  public ValuesViewer getValuesViewer(boolean left_p) {
    ValuesViewer result = left_p? _leftValuesViewer: _rightValuesViewer;
    return result;
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.ui.viewers.AbstractComparisonViewer#handleDispose()
   */
  @Override
  protected void handleDispose() {
    super.handleDispose();
    _lastUserSelection = null;
    _leftModelTreeViewer = null;
    _rightModelTreeViewer = null;
    _leftValuesToolbar = null;
    _rightValuesToolbar = null;
    _leftValuesViewer = null;
    _rightValuesViewer = null;
    _synthesisModelTreeViewer = null;
    _featuresViewer = null;
    _featuresToolbar = null;
    _synthesisSorter = null;
    _toLeftItem = null;
    _toRightItem = null;
    _ignoreLeftItem = null;
    _ignoreRightItem = null;
    _deleteLeftItem = null;
    _deleteRightItem = null;
    _nextItem = null;
    _previousItem = null;
    _unchangedElementsFilter = null;
    _moveOriginsFilter = null;
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.ui.viewers.AbstractComparisonViewer#undoRedoPerformed(boolean)
   */
  @Override
  protected void undoRedoPerformed(final boolean undo_p) {
    super.undoRedoPerformed(undo_p);
    firePropertyChangeEvent(PROPERTY_DIFFERENCE_NUMBERS, null);
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.ui.viewers.AbstractComparisonViewer#inputChanged(java.lang.Object, java.lang.Object)
   */
  @Override
  protected void inputChanged(Object input_p, Object oldInput_p) {
    _featuresViewer.setInput(null);
    _leftValuesViewer.setInput(null);
    _rightValuesViewer.setInput(null);
    _synthesisModelTreeViewer.setInput(input_p);
    _leftModelTreeViewer.setInput(input_p);
    _rightModelTreeViewer.setInput(input_p);
    super.inputChanged(input_p, oldInput_p);
    if (getInput() != null && getInput().isLogEvents())
      getLogger().log(new CompareLogEvent(getEditingDomain(), getComparison()));
  }
  
  /**
   * Merge the current selection to the given side
   * @param toLeft_p whether destination is left or right
   */
  protected void merge(final boolean toLeft_p) {
    final ComparisonSelection selection = getSelection();
    if (selection == null) return;
    final EMFDiffNode input = getInput();
    boolean showMergeImpact = input.isShowMergeImpact();
    boolean coverChildren = input.isDefaultCoverChildren();
    boolean incrementalMode = false;
    List<EMatch> selectedMatches = selection.getSelectedMatches();
    if (selectedMatches.isEmpty()) {
      List<EMatch> treePath = selection.getSelectedTreePath();
      if (!treePath.isEmpty())
        selectedMatches = Collections.singletonList(treePath.get(treePath.size()-1));
    }
    boolean basedOnMatches = !selectedMatches.isEmpty();
    boolean requiresChoices = basedOnMatches;
    if (requiresChoices && selectedMatches.size() == 1) {
      EMatch selectedMatch = selectedMatches.get(0);
      if (!input.hasChildrenForMerge(selectedMatch)) {
        DifferenceKind kind = input.getDifferenceKind(selectedMatch);
        requiresChoices = !(kind.isAddition() || kind.isDeletion());
      }
    }
    if (requiresChoices) {
      // Group of differences
      boolean askAboutChildren = false;
      for (EMatch selectedMatch : selectedMatches) {
        if (input.getDifferenceKind(selectedMatch) == DifferenceKind.COUNTED) {
          coverChildren = true;
          break;
        } else if (input.hasChildrenForMerge(selectedMatch)) {
          askAboutChildren = true;
          break;
        }
      }
      MergeChoiceDialogData choice = new MergeChoiceDialogData(coverChildren,
          input.isDefaultIncrementalMode(), input.isDefaultShowImpact());
      MergeChoicesDialog choicesDialog =
        new MergeChoicesDialog(getShell(), Messages.ComparisonViewer_MergeHeader,
            choice, askAboutChildren);
      int answer = choicesDialog.open();
      if (0 != answer) return;
      showMergeImpact = choice.getShowImpact();
      coverChildren = choice.getCoverChildren();
      incrementalMode = choice.getIncrementalMode();
      if (askAboutChildren)
        input.setDefaultCoverChildren(choice.getCoverChildren());
      input.setDefaultIncrementalMode(choice.getIncrementalMode());
      input.setDefaultShowImpact(choice.getShowImpact());
    }
    final Role destination = input.getRoleForSide(toLeft_p);
    final Collection<IDifference> toMerge = basedOnMatches?
        getDifferencesToMerge(selectedMatches, destination, coverChildren, incrementalMode):
          input.getNonIgnoredDifferences(selection.asDifferencesToMerge());
    final Collection<IDifference> merged = new ArrayList<IDifference>();
    boolean done = false;
    if (!toMerge.isEmpty()) {
      final ImpactInput mergeInput = new ImpactInput(toMerge, toLeft_p, input);
      boolean proceed = true;
      IProgressService progress = PlatformUI.getWorkbench().getProgressService();
      if (showMergeImpact) {
        try {
          progress.busyCursorWhile(new IRunnableWithProgress() {
            /**
             * @see org.eclipse.jface.operation.IRunnableWithProgress#run(IProgressMonitor)
             */
            public void run(final IProgressMonitor monitor_p) throws InvocationTargetException, InterruptedException {
              mergeInput.compute(monitor_p);
            }
          });
          MergeImpactMessageDialog dialog = new MergeImpactMessageDialog(getShell(), mergeInput);
          proceed = dialog.openAndConfirm();
        } catch (Exception exception_p) {
          // Proceed
        }
      }
      if (proceed) {
        try {
          progress.busyCursorWhile(new IRunnableWithProgress() {
            /**
             * @see org.eclipse.jface.operation.IRunnableWithProgress#run(IProgressMonitor)
             */
            public void run(final IProgressMonitor monitor_p) throws InvocationTargetException, InterruptedException {
              Runnable mergeRunnable = new Runnable() {
                /**
                 * @see java.lang.Runnable#run()
                 */
                public void run() {
                  merged.addAll(getComparison().merge(toMerge, destination, true, monitor_p));
                  getUIComparison().setLastActionSelection(selection);
                }
              };
              if (input.isUndoRedoSupported())
                MiscUtil.executeOnDomain(getEditingDomain(), null, mergeRunnable);
              else
                MiscUtil.executeAndForget(getEditingDomain(), mergeRunnable);
            }
          });
          done = true;
        } catch (Exception e) {
          throw new OperationCanceledException(e.getLocalizedMessage()); // Trigger transaction rollback
        }
      }
    } else {
      MessageDialog.openInformation(getShell(), Messages.ComparisonViewer_MergeHeader,
          Messages.ComparisonViewer_NoDiffsToMerge);
    }
    if (!merged.isEmpty() && done) {
      input.setModified(true, toLeft_p);
      firePropertyChangeEvent(CompareEditorInput.DIRTY_STATE, new Boolean(true));
      firePropertyChangeEvent(PROPERTY_DIFFERENCE_NUMBERS, null);
      if (input.isLogEvents())
        getLogger().log(
            new MergeLogEvent(getEditingDomain(), getComparison(), merged, toLeft_p));
    }
  }
  
  /**
   * Navigate to the next/previous difference according to the given flag
   * @param next_p whether navigation must be forward or back
   */
  protected void navigate(boolean next_p) {
    ITreeSelection selection = _synthesisModelTreeViewer.getSelection();
    TreePath current = (selection == null || selection.isEmpty())? TreePath.EMPTY:
      selection.getPaths()[0];
    ComparisonTreeViewer treeViewer = _synthesisModelTreeViewer.getComparisonTreeViewer();
    TreePath newPath = next_p? treeViewer.getNextUserDifference(current):
      treeViewer.getPreviousUserDifference(current);
    if (newPath != null)
      setSelection(new ComparisonSelectionImpl(newPath, getDrivingRole()), true);
  }
  
  /**
   * Return whether the given difference is represented as a virtual "ownership"
   * (an opposite to a containment value presence)
   * @param difference_p a potentially null difference
   */
  protected boolean representAsOwnership(IDifference difference_p) {
    boolean result = false;
    if (difference_p instanceof IReferenceValuePresence) {
      IReferenceValuePresence presence = (IReferenceValuePresence)difference_p;
      EReference ref = presence.getFeature();
      result = !presence.isOrder() && ref != null && ref.isContainment();
    }
    return result;
  }
  
  /**
   * @see org.eclipse.jface.viewers.Viewer#refresh()
   */
  @Override
  public void refresh() {
    _leftModelTreeViewer.refresh();
    _rightModelTreeViewer.refresh();
    _leftValuesViewer.refresh();
    _rightValuesViewer.refresh();
    _featuresViewer.refresh();
    _synthesisModelTreeViewer.refresh();
    super.refresh();
  }
  
  /**
   * Refresh the tools of the viewer. This behavior is centralized instead of being
   * delegated to each tool via listeners in order to increase performance.
   */
  @Override
  protected void refreshTools() {
    ComparisonSelection selection = getSelection();
    EMFDiffNode input = getInput();
    // Merge
    boolean onLeft = false, onRight = false;
    boolean allowDeletion = false;
    boolean allowFreezing = false;
    if (selection != null && input != null) {
      allowFreezing = true;
      IValuePresence presence = selection.asValuePresence();
      if (presence != null && !presence.isMerged()) {
        // Value presence
        DifferenceKind kind = input.getDifferenceKind(presence);
        onLeft = canAddToTheRight(kind);
        onRight = canAddToTheLeft(kind);
        allowDeletion = input.isMany(presence) && !input.isOwnershipOpposite(presence);
      } else if (selection.asFeature() == null) {
        List<EMatch> matches = selection.asMatches();
        if (!matches.isEmpty()) {
          // Matches selected
          if (matches.size() > 1) {
            // Several matches selected
            allowDeletion = true;
            Iterator<EMatch> it = matches.iterator();
            while (it.hasNext() && (!onLeft || !onRight || allowDeletion)) {
              EMatch current = it.next();
              DifferenceKind kind = input.getDifferenceKind(current);
              if (kind.isAddition()) {
                onLeft = onLeft || kind.isLeft(true);
                onRight = onRight || kind.isRight(true);
              } else {
                onLeft = true;
                onRight = true;
                allowDeletion = false;
              }
            }
            allowDeletion = allowDeletion && (onLeft != onRight);
          } else {
            // Only one match selected
            IMatch match = matches.get(0);
            if (input.representAsModification(match) || input.representAsMove(match) ||
                input.getDifferenceKind(match) == DifferenceKind.COUNTED) {
              // Modification or move or inner differences
              onLeft = true;
              onRight = true;
              allowDeletion = false;
            } else {
              // Partial match
              DifferenceKind kind = input.getDifferenceKind(match);
              onLeft = canAddToTheRight(kind);
              onRight = canAddToTheLeft(kind);
              allowDeletion = true;
            }
          }
        }
      }
    }
    if (input != null) {
      _toRightItem.setEnabled(input.isEditable(false) && onLeft);
      _deleteLeftItem.setEnabled(input.isEditable(true) && onLeft && allowDeletion);
      _toLeftItem.setEnabled(input.isEditable(true) && onRight);
      _deleteRightItem.setEnabled(input.isEditable(false) && onRight && allowDeletion);
    }
    _ignoreLeftItem.setEnabled(onLeft && allowFreezing);
    _ignoreRightItem.setEnabled(onRight && allowFreezing);
    super.refreshTools();
  }
  
  /**
   * Set the "base" label provider for representing model elements
   * @param labelProvider_p a potentially null label provider, where null stands for default
   */
  public void setDelegateLabelProvider(ILabelProvider labelProvider_p) {
    List<ContentViewer> viewers = Arrays.<ContentViewer>asList(
        _synthesisModelTreeViewer.getInnerViewer(),
        _leftModelTreeViewer.getInnerViewer(),
        _rightModelTreeViewer.getInnerViewer(),
        _featuresViewer, _leftValuesViewer, _rightValuesViewer);
    for (ContentViewer viewer : viewers) {
      IBaseLabelProvider rawLP = viewer.getLabelProvider();
      if (rawLP instanceof DelegatingLabelProvider) {
        DelegatingLabelProvider delegatingLP = (DelegatingLabelProvider)rawLP;
        delegatingLP.setDelegate(labelProvider_p);
      }
    }
  }
  
  /**
   * @see org.eclipse.jface.viewers.Viewer#setSelection(org.eclipse.jface.viewers.ISelection, boolean)
   */
  @Override
  public void setSelection(ISelection selection_p, boolean reveal_p) {
    final IStructuredSelection emptySelection = new StructuredSelection();
    IStructuredSelection synthesisSelection = emptySelection;
    IStructuredSelection leftModelSelection = emptySelection;
    IStructuredSelection rightModelSelection = emptySelection;
    IStructuredSelection featureSelection = emptySelection;
    IStructuredSelection leftValueSelection = emptySelection;
    IStructuredSelection rightValueSelection = emptySelection;
    if (selection_p instanceof ComparisonSelection) {
      ComparisonSelection selection = (ComparisonSelection)selection_p;
      boolean representAsOwnership = representAsOwnership(selection.asValuePresence());
      // Synthesis
      List<EMatch> matches = selection.asMatches();
      if (!matches.isEmpty()) {
        if (representAsOwnership && matches.size() == 1)
          synthesisSelection = new StructuredSelection(selection.asValuePresence().getValue());
        else
          synthesisSelection = new StructuredSelection(matches);
        leftModelSelection = synthesisSelection;
        rightModelSelection = synthesisSelection;
      }
      // Features: input
      EMatch match = matches.size() == 1? matches.get(0): null;
      if (match != null) {
        if (representAsOwnership(selection.asValuePresence()))
          match = (EMatch)selection.asValuePresence().getValue();
        if (_featuresViewer.getInput() == null ||
            _featuresViewer.getInput().getMatch() != match)
          _featuresViewer.setInput(new FeaturesInput(getInput(), match));
      } else if (!matches.isEmpty()) {
        _featuresViewer.setInput(null);
      }
      // Features: selection
      EStructuralFeature feature = !representAsOwnership? selection.asFeature():
        EMFDiffMergeUIPlugin.getDefault().getOwnershipFeature();
      if (feature == null && matches.size() == 1)
        // If a single match is selected and no feature, select the first relevant feature
        feature = (EStructuralFeature)_featuresViewer.getElementAt(0);
      // Values: input
      ValuesInput valuesInput = feature == null? null:
        new ValuesInput(getInput(), new MatchAndFeatureImpl(match, feature));
      if (valuesInput == null || !valuesInput.equals(_leftValuesViewer.getInput()))
        _leftValuesViewer.setInput(valuesInput);
      if (valuesInput == null || !valuesInput.equals(_rightValuesViewer.getInput()))
        _rightValuesViewer.setInput(valuesInput);
      if (feature != null) {
        featureSelection = new StructuredSelection(feature);
        // Values
        List<EValuePresence> presences = selection.asValuePresences();
        leftValueSelection = new StructuredSelection(presences);
        rightValueSelection = leftValueSelection;
        if (presences.size() == 1) {
          // If a single value presence is selected, select the referenced element
          // in the corresponding model viewer
          IValuePresence presence = presences.get(0);
          if (presence instanceof IReferenceValuePresence) {
            IReferenceValuePresence rvp = (IReferenceValuePresence)presence;
            IMatch value = representAsOwnership(rvp)? rvp.getElementMatch(): rvp.getValue();
            IStructuredSelection rvpSelection = new StructuredSelection(value);
            if (rvp.getPresenceRole() == getInput().getRoleForSide(true))
              leftModelSelection = rvpSelection;
            else
              rightModelSelection = rvpSelection;
          }
        }
      }
    }
    _synthesisModelTreeViewer.setSelection(synthesisSelection, reveal_p);
    if (_isLeftRightSynced) {
      _leftModelTreeViewer.setSelection(leftModelSelection, reveal_p);
      _rightModelTreeViewer.setSelection(rightModelSelection, reveal_p);
    }
    _featuresViewer.setSelection(featureSelection, reveal_p);
    _leftValuesViewer.setSelection(leftValueSelection, reveal_p);
    _rightValuesViewer.setSelection(rightValueSelection, reveal_p);
    _lastUserSelection =
      selection_p instanceof ComparisonSelection? (ComparisonSelection)selection_p:
        new ComparisonSelectionImpl(null, null);
    fireSelectionChanged(new SelectionChangedEvent(this, getSelection()));
  }
  
  /**
   * Configure the sashes of the UI
   * @param upperPart_p the sash form on the up side
   * @param lowerPart_p the sash form on the down side
   */
  protected void setupSashes(final SashForm upperPart_p, final SashForm lowerPart_p) {
    final int[] horizontalWeights = new int[] {3, 2, 2};
    upperPart_p.setWeights(horizontalWeights);
    lowerPart_p.setWeights(horizontalWeights);
    _leftModelTreeViewer.getControl().addControlListener(new ControlListener() {
      /**
       * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
       */
      public void controlResized(ControlEvent e_p) {
        int[] weights = upperPart_p.getWeights();
        lowerPart_p.setWeights(weights);
      }
      /**
       * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
       */
      public void controlMoved(ControlEvent e_p) {
        // Nothing
      }
    });
    _leftValuesViewer.getControl().addControlListener(new ControlListener() {
      /**
       * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
       */
      public void controlResized(ControlEvent e_p) {
        int[] weights = lowerPart_p.getWeights();
        upperPart_p.setWeights(weights);
      }
      /**
       * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
       */
      public void controlMoved(ControlEvent e_p) {
        // Nothing
      }
    });
  }
  
  /**
   * Setup the menus of the feature viewer
   */
  @SuppressWarnings("unused")
  protected void setupFeatureMenus() {
    new ToolItem(_featuresToolbar, SWT.SEPARATOR);
    Menu featureMenu = UIUtil.createMenuTool(_featuresToolbar);
    // Only differences
    final MenuItem onlyDiffs = new MenuItem(featureMenu, SWT.RADIO);
    onlyDiffs.setText(Messages.ComparisonViewer_ShowValueDiffs);
    onlyDiffs.setSelection(true);
    onlyDiffs.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent e_p) {
        _featuresViewer.setShowAllFeatures(false);
        _leftValuesViewer.setShowAllValues(false);
        _rightValuesViewer.setShowAllValues(false);
      }
    });
    // All values
    final MenuItem allValues = new MenuItem(featureMenu, SWT.RADIO);
    allValues.setText(Messages.ComparisonViewer_ShowAllValues);
    allValues.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent e_p) {
        _featuresViewer.setShowAllFeatures(false);
        _leftValuesViewer.setShowAllValues(true);
        _rightValuesViewer.setShowAllValues(true);
      }
    });
    // All values and features
    final MenuItem allFeatures = new MenuItem(featureMenu, SWT.RADIO);
    allFeatures.setText(Messages.ComparisonViewer_ShowAllFeatures);
    allFeatures.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent e_p) {
        _featuresViewer.setShowAllFeatures(true);
        _leftValuesViewer.setShowAllValues(true);
        _rightValuesViewer.setShowAllValues(true);
      }
    });
  }
  
  /**
   * Set up a locking tool for the given viewer
   * @param sideViewer_p a non-null viewer
   * @param left_p whether the viewer is on the left side
   */
  protected void setupLockTool(EnhancedComparisonSideViewer sideViewer_p, final boolean left_p) {
    String lockTooltip = Messages.ComparisonViewer_LockTooltip;
    Image lockImage = EMFDiffMergeUIPlugin.getDefault().getImage(EMFDiffMergeUIPlugin.ImageID.LOCK);
    ToolBar modelToolbar = sideViewer_p.getToolbar();
    final ToolItem lockItem = new ToolItem(modelToolbar, SWT.CHECK);
    lockItem.setImage(lockImage);
    lockItem.setToolTipText(lockTooltip);
    lockItem.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent event_p) {
        getInput().setEditable(!lockItem.getSelection(), left_p);
        refreshTools();
      }
    });
    addPropertyChangeListener(new IPropertyChangeListener() {
      /**
       * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
       */
      public void propertyChange(PropertyChangeEvent event_p) {
        if (PROPERTY_CURRENT_INPUT.equals(event_p.getProperty())) {
          EMFDiffNode input = getInput();
          if (input != null) {
            lockItem.setSelection(!input.isEditable(left_p));
            lockItem.setEnabled(input.isEditionPossible(left_p));
          }
        }
      }
    });
  }
  
  /**
   * Set up the merge tools
   */
  protected void setupMergeTools() {
    // To right
    _toRightItem = new ToolItem(_leftValuesToolbar, SWT.PUSH);
    _toRightItem.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(
        EMFDiffMergeUIPlugin.ImageID.CHECKIN_ACTION));
    _toRightItem.setToolTipText(Messages.ComparisonViewer_MergeRightTooltip);
    _toRightItem.setEnabled(false);
    // Ignore left
    _ignoreLeftItem = new ToolItem(_leftValuesToolbar, SWT.PUSH);
    _ignoreLeftItem.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(
        EMFDiffMergeUIPlugin.ImageID.DONE));
    _ignoreLeftItem.setToolTipText(Messages.ComparisonViewer_IgnoreLeftTooltip);
    _ignoreLeftItem.setEnabled(false);
    // Delete left
    _deleteLeftItem = new ToolItem(_leftValuesToolbar, SWT.PUSH);
    _deleteLeftItem.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(
        EMFDiffMergeUIPlugin.ImageID.DELETE));
    _deleteLeftItem.setToolTipText(Messages.ComparisonViewer_DeleteLeftTooltip);
    _deleteLeftItem.setEnabled(false);
    // To left
    _toLeftItem = new ToolItem(_rightValuesToolbar, SWT.PUSH);
    _toLeftItem.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(
        EMFDiffMergeUIPlugin.ImageID.CHECKOUT_ACTION));
    _toLeftItem.setToolTipText(Messages.ComparisonViewer_MergeLeftTooltip);
    _toLeftItem.setEnabled(false);
    // Ignore right
    _ignoreRightItem = new ToolItem(_rightValuesToolbar, SWT.PUSH);
    _ignoreRightItem.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(
        EMFDiffMergeUIPlugin.ImageID.DONE));
    _ignoreRightItem.setToolTipText(Messages.ComparisonViewer_IgnoreRightTooltip);
    _ignoreRightItem.setEnabled(false);
    // Delete right
    _deleteRightItem = new ToolItem(_rightValuesToolbar, SWT.PUSH);
    _deleteRightItem.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(
        EMFDiffMergeUIPlugin.ImageID.DELETE));
    _deleteRightItem.setToolTipText(Messages.ComparisonViewer_DeleteRightTooltip);
    _deleteRightItem.setEnabled(false);
    // Merge behavior
    SelectionListener mergeToLeft = new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent event_p) {
        merge(true);
      }
    };
    SelectionListener mergeToRight = new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent event_p) {
        merge(false);
      }
    };
    _toLeftItem.addSelectionListener(mergeToLeft);
    _deleteLeftItem.addSelectionListener(mergeToLeft);
    _toRightItem.addSelectionListener(mergeToRight);
    _deleteRightItem.addSelectionListener(mergeToRight);
    _ignoreLeftItem.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent event_p) {
        ignore(true);
      }
    });
    _ignoreRightItem.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent event_p) {
        ignore(false);
      }
    });
  }
  
  /**
   * Set up the navigation tools
   */
  protected void setupNavigationTools() {
    // Next
    _nextItem = new ToolItem(_featuresToolbar, SWT.PUSH);
    _nextItem.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(
        EMFDiffMergeUIPlugin.ImageID.NEXT_DIFF_NAV));
    _nextItem.setToolTipText(Messages.ComparisonViewer_NextTooltip);
    _nextItem.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent event_p) {
        navigate(true);
      }
    });
    // Previous
    _previousItem = new ToolItem(_featuresToolbar, SWT.PUSH);
    _previousItem.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(
        EMFDiffMergeUIPlugin.ImageID.PREV_DIFF_NAV));
    _previousItem.setToolTipText(Messages.ComparisonViewer_PreviousTooltip);
    _previousItem.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent event_p) {
        navigate(false);
      }
    });
  }
  
  /**
   * Add listeners for synchronizing the viewers
   */
  protected void setupSynchronizationListeners() {
    // Selection synchronization: Synthesis[user] -> Global
    _synthesisModelTreeViewer.getTree().addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event_p) {
        IStructuredSelection selection = _synthesisModelTreeViewer.getSelection();
        if (!selection.isEmpty())
          ComparisonViewer.this.setSelection(new ComparisonSelectionImpl(
              selection.toList(), getDrivingRole()), true);
      }
    });
    // Selection synchronization: Models[user] -> Global
    _leftModelTreeViewer.getTree().addSelectionListener(
        new ComparisonSideSelectionListener(true));
    _rightModelTreeViewer.getTree().addSelectionListener(
        new ComparisonSideSelectionListener(false));
    // Selection synchronization: Values[user] -> Model
    _leftValuesViewer.getTable().addSelectionListener(new ValuesSelectionListener(true));
    _rightValuesViewer.getTable().addSelectionListener(new ValuesSelectionListener(false));
    // Selection synchronization: Features[user] -> Values
    _featuresViewer.getTable().addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent event_p) {
        IStructuredSelection selection = (IStructuredSelection)_featuresViewer.getSelection();
        if (selection.size() == 1) {
          IMatch match = _featuresViewer.getInput() == null? null:
            _featuresViewer.getInput().getMatch();
          if (match instanceof EMatch) {
            EStructuralFeature feature = (EStructuralFeature)selection.getFirstElement();
            MatchAndFeature newInputDetails = new MatchAndFeatureImpl((EMatch)match, feature);
            setSelection(new ComparisonSelectionImpl(newInputDetails, getDrivingRole()), true);
          }
        }
      }
    });
  }
  
  /**
   * Setup the menus of the synthesis viewer
   */
  @SuppressWarnings("unused")
  protected Menu setupSynthesisMenus() {
    ToolBar synthesisToolBar = _synthesisModelTreeViewer.getToolbar();
    Menu synthesisMenu = UIUtil.createMenuTool(synthesisToolBar);
    // Show uncounted elements
    final MenuItem showUnchanged = new MenuItem(synthesisMenu, SWT.CHECK);
    showUnchanged.setText(Messages.ComparisonViewer_ShowUncountedMenuItem);
    showUnchanged.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent e_p) {
        if (showUnchanged.getSelection())
          _synthesisModelTreeViewer.getComparisonTreeViewer().removeFilter(_unchangedElementsFilter);
        else
          _synthesisModelTreeViewer.getComparisonTreeViewer().addFilter(_unchangedElementsFilter);
      }
    });
    // Show moved elements on one side only
//    final MenuItem showMovedOnce = new MenuItem(synthesisMenu, SWT.CHECK);
//    showMovedOnce.setText(Messages.ComparisonViewer_ShowMovesMenuItem);
//    showMovedOnce.setSelection(false);
//    _synthesisModelTreeViewer.getComparisonTreeViewer().addFilter(_moveOriginsFilter);
//    showMovedOnce.addSelectionListener(new SelectionAdapter() {
//      /**
//       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
//       */
//      @Override
//      public void widgetSelected(SelectionEvent e_p) {
//        if (showMovedOnce.getSelection())
//          _synthesisModelTreeViewer.getComparisonTreeViewer().removeFilter(_moveOriginsFilter);
//        else
//          _synthesisModelTreeViewer.getComparisonTreeViewer().addFilter(_moveOriginsFilter);
//      }
//    });
    new MenuItem(synthesisMenu, SWT.SEPARATOR);
    // Show additions
    final MenuItem showAdditions = new MenuItem(synthesisMenu, SWT.CHECK);
    showAdditions.setText(Messages.ComparisonViewer_CountAddLeftMenuItem);
    showAdditions.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(ImageID.OUT_ADD_STAT));
    showAdditions.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent e_p) {
        changeCounting(UserDifferenceKind.PRESENCE_LEFT, showAdditions.getSelection());
      }
    });
    // Show deletions
    final MenuItem showDeletions = new MenuItem(synthesisMenu, SWT.CHECK);
    showDeletions.setText(Messages.ComparisonViewer_CountAddRightMenuItem);
    showDeletions.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(ImageID.INC_ADD_STAT));
    showDeletions.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent e_p) {
        changeCounting(UserDifferenceKind.PRESENCE_RIGHT, showDeletions.getSelection());
      }
    });
    // Show Moves
    final MenuItem showMoves = new MenuItem(synthesisMenu, SWT.CHECK);
    showMoves.setText(Messages.ComparisonViewer_CountMovesMenuItem);
    showMoves.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(ImageID.MODIFIED_STAT));
    showMoves.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent e_p) {
        changeCounting(UserDifferenceKind.MOVE, showMoves.getSelection());
      }
    });
    // Show proper differences
    final MenuItem showNoContainmentDiffs = new MenuItem(synthesisMenu, SWT.CHECK);
    showNoContainmentDiffs.setText(Messages.ComparisonViewer_CountProperMenuItem);
    showNoContainmentDiffs.setImage(EMFDiffMergeUIPlugin.getDefault().getImage(ImageID.MODIFIED_STAT));
    showNoContainmentDiffs.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent e_p) {
        changeCounting(UserDifferenceKind.NO_CONTAINMENT, showNoContainmentDiffs.getSelection());
      }
    });
    // Use custom icons
    new MenuItem(synthesisMenu, SWT.SEPARATOR);
    final MenuItem useCustomIconsItem = new MenuItem(synthesisMenu, SWT.CHECK);
    useCustomIconsItem.setText(Messages.ComparisonViewer_IconsMenuItem);
    useCustomIconsItem.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent e_p) {
        EMFDiffNode input = getInput();
        if (input != null) {
          input.setUseCustomIcons(useCustomIconsItem.getSelection());
          _synthesisModelTreeViewer.refresh();
          _featuresViewer.refresh();
          _leftValuesViewer.refresh();
          _rightValuesViewer.refresh();
        }
      }
    });
    // Show merge impact
    final MenuItem showImpactItem = new MenuItem(synthesisMenu, SWT.CHECK);
    showImpactItem.setText(Messages.ComparisonViewer_ImpactMenuItem);
    showImpactItem.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent e_p) {
        boolean showImpact = showImpactItem.getSelection();
        EMFDiffNode input = getInput();
        if (input != null) {
          input.setShowMergeImpact(showImpact);
          input.setDefaultShowImpact(showImpact);
        }
      }
    });
    // Support undo/redo
    final MenuItem supportUndoRedoItem = new MenuItem(synthesisMenu, SWT.CHECK);
    supportUndoRedoItem.setText(Messages.ComparisonViewer_SupportUndoRedoMenuItem);
    supportUndoRedoItem.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent e_p) {
        EMFDiffNode input = getInput();
        if (input != null)
          input.setUndoRedoSupported(supportUndoRedoItem.getSelection());
      }
    });
    // Log events
    final MenuItem logEventsItem = new MenuItem(synthesisMenu, SWT.CHECK);
    logEventsItem.setText(Messages.ComparisonViewer_LogEventsMenuItem);
    logEventsItem.addSelectionListener(new SelectionAdapter() {
      /**
       * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(SelectionEvent e_p) {
        boolean logEvents = logEventsItem.getSelection();
        EMFDiffNode input = getInput();
        if (input != null) {
          input.setLogEvents(logEvents);
          if (logEvents)
            getLogger().log(new CompareLogEvent(getEditingDomain(), getComparison()));
        }
      }
    });
    addPropertyChangeListener(new IPropertyChangeListener() {
      /**
       * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
       */
      public void propertyChange(PropertyChangeEvent event_p) {
        if (PROPERTY_CURRENT_INPUT.equals(event_p.getProperty())) {
          EMFDiffNode input = getInput();
          if (input != null) {
            showAdditions.setSelection(input.counts(UserDifferenceKind.PRESENCE_LEFT));
            showDeletions.setSelection(input.counts(UserDifferenceKind.PRESENCE_RIGHT));
            showMoves.setSelection(input.counts(UserDifferenceKind.MOVE));
            showNoContainmentDiffs.setSelection(input.counts(UserDifferenceKind.NO_CONTAINMENT));
            logEventsItem.setSelection(input.isLogEvents());
            showImpactItem.setSelection(input.isShowMergeImpact());
            supportUndoRedoItem.setSelection(input.isUndoRedoSupported());
            supportUndoRedoItem.setEnabled(input.getEditingDomain() != null);
            useCustomIconsItem.setSelection(input.usesCustomIcons());
          }
        }
      }
    });
    return synthesisMenu;
  }
  
  /**
   * Set up the different tool bars
   */
  protected void setupToolbars() {
    // Synthesis view representation
    setupSynthesisTools();
    // Navigation
    setupNavigationTools();
    // Model locking
    setupLockTool(_leftModelTreeViewer, true);
    setupLockTool(_rightModelTreeViewer, false);
    // Merge tools
    setupMergeTools();
    // Menus
    setupSynthesisMenus();
    setupFeatureMenus();
    // Reaction to selection change
    addSelectionChangedListener(new ISelectionChangedListener() {
      /**
       * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
       */
      public void selectionChanged(SelectionChangedEvent event_p) {
        refreshTools();
      }
    });
    refreshTools();
  }
  
  /**
   * Set up the tools related to the representation of the synthesis view
   */
  @SuppressWarnings("unused")
  protected void setupSynthesisTools() {
    ToolBar synthesisToolbar = _synthesisModelTreeViewer.getToolbar();
    new ToolItem(synthesisToolbar, SWT.SEPARATOR);
    createInconsistencyTool(synthesisToolbar);
    createExpandTool(synthesisToolbar);
    createCollapseTool(synthesisToolbar);
    createSortTool(synthesisToolbar);
    createSyncTool(synthesisToolbar);
    new ToolItem(synthesisToolbar, SWT.SEPARATOR);
  }
  
  
  /**
   * A selection listener for Values[user] -> Global synchronization
   */
  protected class ValuesSelectionListener extends SelectionAdapter {
    /** Whether the viewer that is being tracked is on the left-hand side  */
    private final boolean _sideIsLeft;
    /**
     * Constructor
     * @param sideIsLeft_p whether the viewer that is being tracked is on the left-hand side
     */
    public ValuesSelectionListener(boolean sideIsLeft_p) {
      _sideIsLeft = sideIsLeft_p;
    }
    /**
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    @Override
    public void widgetSelected(SelectionEvent event_p) {
      ValuesViewer valuesViewer = _sideIsLeft? _leftValuesViewer: _rightValuesViewer;
      IStructuredSelection selection = valuesViewer.getSelection();
      if (!selection.isEmpty()) {
        if (selection.getFirstElement() instanceof EObject) // Skip attribute values
          ComparisonViewer.this.setSelection(
              new ComparisonSelectionImpl(
                  selection.toList(), getInput().getRoleForSide(_sideIsLeft)), true);
      }
    }
  }
  
  
  /**
   * A selection listener for Model[user] -> Global synchronization
   */
  protected class ComparisonSideSelectionListener extends SelectionAdapter {
    /** Whether the viewer that is being tracked is on the left-hand side  */
    private final boolean _sideIsLeft;
    /**
     * Constructor
     * @param sideIsLeft_p whether the viewer that is being tracked is on the left-hand side
     */
    public ComparisonSideSelectionListener(boolean sideIsLeft_p) {
      _sideIsLeft = sideIsLeft_p;
    }
    /**
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    @Override
    public void widgetSelected(SelectionEvent event_p) {
      if (_isLeftRightSynced) {
        EnhancedComparisonSideViewer modelViewer = _sideIsLeft? _leftModelTreeViewer:
          _rightModelTreeViewer;
        IStructuredSelection selection = modelViewer.getSelection();
        if (!selection.isEmpty())
          ComparisonViewer.this.setSelection(new ComparisonSelectionImpl(
              selection.toList(), getInput().getRoleForSide(_sideIsLeft)), true);
      }
    }
  }
  
  /**
   * A message dialog which uses MergeImpactViewer.
   */
  protected class MergeImpactMessageDialog extends MessageDialog {
    /** The non-null input */
    private final ImpactInput _dialogInput;
    
    /**
     * Constructor
     * @param parentShell_p a non-null shell
     */
    public MergeImpactMessageDialog(Shell parentShell_p, ImpactInput input_p) {
      super(parentShell_p, Messages.ComparisonViewer_MergeHeader, null,
          String.format(
              Messages.ComparisonViewer_ImpactDescription, input_p.isOnTheLeft()?
                  Messages.ComparisonViewer_Left: Messages.ComparisonViewer_Right),
          MessageDialog.INFORMATION,
          new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
      _dialogInput = input_p;
      setShellStyle(getShellStyle() | SWT.RESIZE);
    }
    
    /**
     * @see MessageDialog#createCustomArea(Composite)
     */
    @Override
    protected Control createCustomArea(Composite parent_p) {
      MergeImpactViewer viewer = new MergeImpactViewer(parent_p, getResourceManager());
      // Reuse label provider from synthesis viewer
      IBaseLabelProvider lProvider = _synthesisModelTreeViewer.getInnerViewer().getLabelProvider();
      if (lProvider instanceof DelegatingLabelProvider)
        viewer.setDelegateLabelProvider(((DelegatingLabelProvider)lProvider).getDelegate());
      viewer.setInput(_dialogInput);
      return viewer.getControl();
    }
    
    /**
     * Open the dialog and return whether the user pressed OK
     */
    public boolean openAndConfirm() {
      return open() == 0;
    }
  }
  
}
