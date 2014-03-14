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
package org.eclipse.emf.diffmerge.impl.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.diffmerge.Messages;
import org.eclipse.emf.diffmerge.api.IComparison;
import org.eclipse.emf.diffmerge.api.IDiffPolicy;
import org.eclipse.emf.diffmerge.api.IMapping;
import org.eclipse.emf.diffmerge.api.IMatch;
import org.eclipse.emf.diffmerge.api.IMergePolicy;
import org.eclipse.emf.diffmerge.api.Role;
import org.eclipse.emf.diffmerge.api.diff.IAttributeValuePresence;
import org.eclipse.emf.diffmerge.api.diff.IDifference;
import org.eclipse.emf.diffmerge.api.diff.IElementPresence;
import org.eclipse.emf.diffmerge.api.diff.IMergeableDifference;
import org.eclipse.emf.diffmerge.api.diff.IReferenceValuePresence;
import org.eclipse.emf.diffmerge.api.diff.IValuePresence;
import org.eclipse.emf.diffmerge.api.scopes.IFeaturedModelScope;
import org.eclipse.emf.diffmerge.util.structures.FArrayList;
import org.eclipse.emf.diffmerge.util.structures.IEqualityTester;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;


/**
 * An operation which builds differences for a comparison.
 * @author Olivier Constant
 */
public class DiffOperation extends AbstractExpensiveOperation {
  
  /** The non-null diff policy */
  private final IDiffPolicy _diffPolicy;
  
  /** The non-null merge policy */
  private final IMergePolicy _mergePolicy;
  
  /** The non-null comparison whose differences are being built */
  private final IComparison.Editable _comparison;
  
  
  /**
   * Constructor based on a comparison with a predefined mapping
   * @param comparison_p a non-null comparison whose mapping is already built
   * @param diffPolicy_p a non-null diff policy
   * @param mergePolicy_p a non-null merge policy
   */
  public DiffOperation(IComparison.Editable comparison_p, IDiffPolicy diffPolicy_p,
      IMergePolicy mergePolicy_p) {
    super();
    _comparison = comparison_p;
    _diffPolicy = diffPolicy_p;
    _mergePolicy = mergePolicy_p;
  }
  
  /**
   * Create the differences related to the attributes for the given match
   * @param match_p a non-null, non-partial match
   */
  protected void createAllAttributeDifferences(IMatch match_p) {
    assert match_p != null && !match_p.isPartial();
    EClass eClass = match_p.get(Role.TARGET).eClass();
    for (EAttribute attribute : eClass.getEAllAttributes()) {
      if (getDiffPolicy().coverFeature(attribute))
        createAttributeDifferences(match_p, attribute);
    }
  }
  
  /**
   * Create the differences related to the given attribute for the given match
   * @param match_p a non-null, non-partial match
   * @param attribute_p a non-null attribute
   */
  protected void createAttributeDifferences(IMatch match_p, EAttribute attribute_p) {
    assert match_p != null && !match_p.isPartial() && attribute_p != null;
    IFeaturedModelScope targetScope = getComparison().getScope(Role.TARGET);
    IFeaturedModelScope referenceScope = getComparison().getScope(Role.REFERENCE);
    EObject target = match_p.get(Role.TARGET);
    EObject reference = match_p.get(Role.REFERENCE);
    List<Object> targetValues = targetScope.get(target, attribute_p);
    List<Object> referenceValues = referenceScope.get(reference, attribute_p);
    List<Object> remainingTargetValues = new ArrayList<Object>(targetValues);
    List<Object> remainingReferenceValues = new ArrayList<Object>(referenceValues);
    boolean checkOrder = attribute_p.isMany() && getDiffPolicy().considerOrdered(attribute_p);
    int maxIndex = -1;
    for (Object targetValue : targetValues) {
      ObjectAndIndex matchingReferenceValue = findEqualAttributeValue(
          attribute_p, targetValue, remainingReferenceValues);
      if (matchingReferenceValue.getObject() != null) {
        if (checkOrder) {
          if (matchingReferenceValue.getIndex() < maxIndex) {
            // Ordering difference
            createAttributeOrderDifference(
                match_p, attribute_p, targetValue, matchingReferenceValue.getObject());
            checkOrder = false;
          } else {
            maxIndex = matchingReferenceValue.getIndex();
          }
        }
        remainingTargetValues.remove(targetValue);
        remainingReferenceValues.remove(matchingReferenceValue.getObject());
      }
    }
    for (Object remainingTargetValue : remainingTargetValues) {
      if (getDiffPolicy().coverValue(remainingTargetValue, attribute_p))
        createAttributeValueDifference(match_p, attribute_p, remainingTargetValue,
            Role.TARGET, false);
    }
    for (Object remainingReferenceValue : remainingReferenceValues) {
      if (getDiffPolicy().coverValue(remainingReferenceValue, attribute_p))
        createAttributeValueDifference(match_p, attribute_p, remainingReferenceValue,
            Role.REFERENCE, false);
    }
  }
  
  /**
   * Create the attribute order difference corresponding to the given link
   * (holder, reference, value).
   * @param elementMatch_p a non-null match
   * @param attribute_p a non-null attribute
   * @param targetValue_p a non-null object
   * @param referenceValue_p a non-null object
   */
  protected void createAttributeOrderDifference(IMatch elementMatch_p,
      EAttribute attribute_p, Object targetValue_p, Object referenceValue_p) {
    createAttributeValueDifference(elementMatch_p, attribute_p, targetValue_p,
        Role.TARGET, true);
    createAttributeValueDifference(elementMatch_p, attribute_p, referenceValue_p,
        Role.REFERENCE, true);
  }
  
  /**
   * Create the attribute difference corresponding to the given link (holder,
   * attribute, value) on the given role.
   * @param elementMatch_p a non-null, non-partial match
   * @param attribute_p a non-null attribute
   * @param value_p a non-null value
   * @param role_p a non-null role which is TARGET or REFERENCE
   * @param isOrder_p whether the value presence is solely due to ordering
   * @return a non-null attribute value presence
   */
  protected IAttributeValuePresence createAttributeValueDifference(
      IMatch elementMatch_p, EAttribute attribute_p, Object value_p, Role role_p, boolean isOrder_p) {
    IAttributeValuePresence result = getComparison().newAttributeValuePresence(
            elementMatch_p, attribute_p, value_p, role_p, isOrder_p);
    IAttributeValuePresence symmetrical = result.getSymmetrical();
    if (symmetrical != null)
      setSymmetricalValuePresenceDependencies(result, symmetrical);
    if (getComparison().isThreeWay())
      setThreeWayProperties(result);
    return result;
  }
  
  /**
   * Create the differences related to ownership if needed
   * @param match_p a non-null, non-partial match
   */
  protected void createOwnershipDifferences(IMatch match_p) {
    assert match_p != null && !match_p.isPartial();
    for (Role role : Arrays.asList(Role.TARGET, Role.REFERENCE)) {
      IMatch parentMatch = getComparison().getContainerOf(match_p, role);
      // An ownership difference needs only be created if the container
      // is unmatched, otherwise it is already handled by container refs
      if (parentMatch != null && parentMatch.isPartial()) {
        EObject element = match_p.get(role);
        EReference containment = getComparison().getScope(role).getContainment(element);
        createReferenceValueDifference(parentMatch, containment, match_p, role, false);
      }
    }
  }
  
  /**
   * Create the differences related to the non-container references for the
   * given match
   * @param match_p a non-null, non-partial match
   */
  protected void createAllReferenceDifferences(IMatch match_p) {
    assert match_p != null && !match_p.isPartial();
    EClass eClass = match_p.get(Role.TARGET).eClass();
    for (EReference reference : eClass.getEAllReferences()) {
      if (!reference.isContainer() && getDiffPolicy().coverFeature(reference))
        createReferenceDifferences(match_p, reference);
    }
  }
  
  /**
   * Create the differences related to the given reference for the given match
   * @param match_p a non-null, non-partial match
   * @param reference_p a non-null, non-container reference
   */
  protected void createReferenceDifferences(IMatch match_p, EReference reference_p) {
    assert match_p != null && !match_p.isPartial() && reference_p != null;
    assert !reference_p.isContainer();
    // Get reference values in different roles
    IFeaturedModelScope targetScope = getComparison().getScope(Role.TARGET);
    IFeaturedModelScope referenceScope = getComparison().getScope(Role.REFERENCE);
    EObject targetElement = match_p.get(Role.TARGET);
    EObject referenceElement = match_p.get(Role.REFERENCE);
    List<EObject> targetValues = targetScope.get(targetElement, reference_p);
    List<EObject> referenceValues = referenceScope.get(referenceElement, reference_p);
    List<EObject> remainingReferenceValues = new FArrayList<EObject>(
        referenceValues, IEqualityTester.BY_REFERENCE);
    boolean checkOrder = reference_p.isMany() && getDiffPolicy().considerOrdered(reference_p);
    int maxIndex = -1;
    // Check which ones match
    List<IMatch> isolatedTargetMatches = new FArrayList<IMatch>();
    for (EObject targetValue : targetValues) {
      // For every value in TARGET, get its corresponding match (if none, uncovered)
      IMatch targetValueMatch = getMapping().getMatchFor(targetValue, Role.TARGET);
      if (targetValueMatch != null) {
        // Get the matching value in REFERENCE
        EObject matchReference = targetValueMatch.get(Role.REFERENCE);
        boolean isIsolated = matchReference == null;
        if (!isIsolated) {
          // Check value presence and ordering
          int index = remainingReferenceValues.indexOf(matchReference);
          isIsolated = index < 0;
          if (checkOrder && !isIsolated) {
            if (index < maxIndex) {
              // Ordering difference
              createReferenceOrderDifference(match_p, reference_p, targetValueMatch);
              checkOrder = false;
            } else {
              maxIndex = index;
            }
          }
        }
        if (isIsolated)
          // None found or not in referenced values: mark as isolated
          isolatedTargetMatches.add(targetValueMatch);
        else
          remainingReferenceValues.remove(matchReference);
      }
    }
    // For every remaining value in REFERENCE, get its corresponding isolated match
    // if the value is covered
    List<IMatch> isolatedReferenceMatches = new FArrayList<IMatch>();
    for (EObject remainingReferenceValue : remainingReferenceValues) {
      IMatch referenceValueMatch = getMapping().getMatchFor(
          remainingReferenceValue, Role.REFERENCE);
      if (referenceValueMatch != null)
        isolatedReferenceMatches.add(referenceValueMatch);      
    }
    // Create differences for isolated values
    for (IMatch isolatedTargetMatch : isolatedTargetMatches) {
      if (getDiffPolicy().coverMatch(isolatedTargetMatch))
        createReferenceValueDifference(match_p, reference_p, isolatedTargetMatch,
          Role.TARGET, false);
    }
    for (IMatch isolatedReferenceMatch : isolatedReferenceMatches) {
      if (getDiffPolicy().coverMatch(isolatedReferenceMatch))
        createReferenceValueDifference(match_p, reference_p, isolatedReferenceMatch,
            Role.REFERENCE, false);
    }
  }
  
  /**
   * Create the reference order difference corresponding to the given link
   * (holder, reference, value).
   * @param elementMatch_p a non-null match
   * @param reference_p a non-null reference
   * @param valueMatch_p a non-null match
   */
  protected void createReferenceOrderDifference(IMatch elementMatch_p,
      EReference reference_p, IMatch valueMatch_p) {
    createReferenceValueDifference(elementMatch_p, reference_p, valueMatch_p,
        Role.TARGET, true);
    createReferenceValueDifference(elementMatch_p, reference_p, valueMatch_p,
        Role.REFERENCE, true);
  }
  
  /**
   * Create the reference difference corresponding to the given link (holder,
   * reference, value) on the given role.
   * @param elementMatch_p a non-null match
   * @param reference_p a non-null, non-container reference
   * @param valueMatch_p a non-null match
   * @param role_p a non-null role which is TARGET or REFERENCE
   * @param isOrder_p whether the value presence is solely due to ordering
   * @return a non-null reference value presence
   */
  protected IReferenceValuePresence createReferenceValueDifference(
      IMatch elementMatch_p, EReference reference_p, IMatch valueMatch_p,
      Role role_p, boolean isOrder_p) {
    IReferenceValuePresence result = getComparison().newReferenceValuePresence(
        elementMatch_p, reference_p, valueMatch_p, role_p, isOrder_p);
    setReferencedValueDependencies(result);
    if (getComparison().isThreeWay())
      setThreeWayProperties(result);
    return result;
  }
  
  /**
   * Create differences based on the mapping between the model scopes compared
   */
  protected void createDifferences() {
    for (IMatch match : getMapping().getContents()) {
      checkProgress();
      if (getDiffPolicy().coverMatch(match))
        createTechnicalDifferences(match);
      getMonitor().worked(1);
    }
  }
  
  /**
   * Create the technical differences corresponding to the given non-partial
   * match, focusing on the content of the elements matched
   * @param match_p a non-null, non-partial match
   */
  protected void createContentDifferences(IMatch match_p) {
    assert match_p != null && !match_p.isPartial();
    createAllAttributeDifferences(match_p);
    createAllReferenceDifferences(match_p);
    createOwnershipDifferences(match_p);
  }
  
  /**
   * Create the technical differences corresponding to the given match
   * @param match_p a non-null match
   */
  protected void createTechnicalDifferences(IMatch match_p) {
    assert match_p != null;
    if (match_p.isPartial()) {
      getOrCreateElementPresence(match_p);
    } else {
      createContentDifferences(match_p);
    }
  }
  
  /**
   * Return a value in the given collection of values which is considered equal
   * to the given value for the given attribute and its index in the collection
   * @param attribute_p a non-null attribute
   * @param value_p a non-null value which is type-compatible with the attribute
   * @param candidates_p a non-null collection of candidate values
   * @return a non-null object
   */
  protected ObjectAndIndex findEqualAttributeValue(EAttribute attribute_p, Object value_p,
      Collection<? extends Object> candidates_p) {
    int i = 0;
    for (Object candidate : candidates_p) {
      if (getDiffPolicy().considerEqual(value_p, candidate, attribute_p))
        return new ObjectAndIndex(candidate, i);
      i++;
    }
    return new ObjectAndIndex();
  }
  
  /**
   * Return the comparison which is being built
   * @return a non-null comparison
   */
  public IComparison.Editable getComparison() {
    return _comparison;
  }
  
  /**
   * Return the diff policy
   * @return a non-null diff policy
   */
  protected IDiffPolicy getDiffPolicy() {
    return _diffPolicy;
  }
  
  /**
   * Return the merge policy
   * @return a non-null merge policy
   */
  protected IMergePolicy getMergePolicy() {
    return _mergePolicy;
  }
  
  /**
   * Return the mapping of the comparison which is being built
   */
  protected IMapping getMapping() {
    return getComparison().getMapping();
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.util.IExpensiveOperation#getOperationName()
   */
  public String getOperationName() {
    return Messages.DiffBuilder_Task_Main;
  }
  
  /**
   * Create and return the element presence difference corresponding to
   * the given partial match, if allowed. If it already exists, just return it.
   * @param match_p a non-null, partial match
   * @return a potentially null element presence
   */
  protected IElementPresence getOrCreateElementPresence(IMatch match_p) {
    assert match_p != null && match_p.isPartial();
    IElementPresence result = match_p.getElementPresenceDifference();
    if (result == null && getDiffPolicy().coverMatch(match_p)) {
      Role presenceRole = match_p.getUncoveredRole().opposite();
      IMatch ownerMatch = getComparison().getContainerOf(match_p, presenceRole);
      result = getComparison().newElementPresence(match_p, ownerMatch);
      if (getComparison().isThreeWay() && !match_p.coversRole(Role.ANCESTOR))
        ((IDifference.Editable)result).markAsDifferentFromAncestor();
      setElementPresenceDependencies(result);
    }
    return result;
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.impl.helpers.AbstractExpensiveOperation#getWorkAmount()
   */
  @Override
  protected int getWorkAmount() {
    return 1 + getMapping().size();
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.util.IExpensiveOperation#run()
   */
  public IStatus run() {
    getMonitor().worked(1);
    createDifferences();
    return Status.OK_STATUS;
  }
  
  /**
   * Set dependencies between differences of type element presence exclusively
   * @param presence_p a non-null element presence
   */
  protected void setElementPresenceDependencies(IElementPresence presence_p) {
    Role presenceRole = presence_p.getPresenceRole();
    if (!presence_p.isRoot()) {
      IMatch ownerMatch = presence_p.getOwnerMatch();
      if (getMergePolicy().bindPresenceToOwnership(_comparison.getScope(presenceRole.opposite())) &&
          ownerMatch != null && ownerMatch.isPartial()) {
        IElementPresence ownerPresence = getOrCreateElementPresence(ownerMatch);
        if (ownerPresence != null) {
          // Child addition requires container addition
          ((IMergeableDifference.Editable)presence_p).markRequires(
              ownerPresence, presenceRole.opposite());
          // Container deletion requires child deletion
          ((IMergeableDifference.Editable)ownerPresence).markRequires(
              presence_p, presenceRole);
        }
      }
    }
    // Grouped addition according to policy
    Collection<EObject> additionPeers = getMergePolicy().getAdditionGroup(
        presence_p.getElement(), getComparison().getScope(presenceRole));
    for (EObject peer : additionPeers) {
      IMatch peerMatch = getMapping().getMatchFor(peer, presenceRole);
      if (peerMatch != null && peerMatch.isPartial()) {
        IElementPresence peerPresence = getOrCreateElementPresence(peerMatch);
        if (peerPresence != null) {
          // Element addition requires group member addition
          ((IMergeableDifference.Editable)presence_p).markRequires(
              peerPresence, presenceRole.opposite());
          // Group member deletion requires element deletion
          ((IMergeableDifference.Editable)peerPresence).markRequires(
              presence_p, presenceRole);
        }
      }
    }
    // Grouped deletion according to policy
    Collection<EObject> deletionPeers = getMergePolicy().getDeletionGroup(
        presence_p.getElement(), getComparison().getScope(presenceRole));
    for (EObject peer : deletionPeers) {
      IMatch peerMatch = getMapping().getMatchFor(peer, presenceRole);
      if (peerMatch != null && peerMatch.isPartial()) {
        IElementPresence peerPresence = getOrCreateElementPresence(peerMatch);
        if (peerPresence != null) {
          // Element deletion requires group member deletion
          ((IMergeableDifference.Editable)presence_p).markRequires(
              peerPresence, presenceRole);
        }
      }
    }
  }
  
  /**
   * Set dependencies between opposite differences of type reference value presence,
   * that is, reference differences on the same link
   * @param first_p a non-null reference value presence
   * @param second_p a non-null reference value presence which is opposite to first_p
   */
  protected void setOppositeReferenceDependencies(
      IReferenceValuePresence first_p, IReferenceValuePresence second_p) {
    assert first_p.isOppositeOf(second_p);
    IMergeableDifference.Editable first = (IMergeableDifference.Editable)first_p;
    IMergeableDifference.Editable second = (IMergeableDifference.Editable)second_p;
    // Opposite diffs are implicitly equivalent except for non-many references
    // which bring their own constraints and must therefore be merged explicitly
    Role presenceRole = first_p.getPresenceRole();
    if (second_p.getFeature().isMany()) {
      first.markImplies(second_p, presenceRole);
      first.markImplies(second_p, presenceRole.opposite());
    } else {
      first.markRequires(second_p, presenceRole);
      first.markRequires(second_p, presenceRole.opposite());
    }
    if (first_p.getFeature().isMany()) {
      second.markImplies(first_p, presenceRole);
      second.markImplies(first_p, presenceRole.opposite());
    } else {
      second.markRequires(first_p, presenceRole);
      second.markRequires(first_p, presenceRole.opposite());
    }
  }
  
  /**
   * Set dependencies between a reference value presence and the presence of the value
   * @param referenceDiff_p a non-null reference presence to a partial match
   */
  protected void setPartialReferencedValueDependencies(
      IReferenceValuePresence referenceDiff_p) {
    assert referenceDiff_p.getValue().isPartial();
    IElementPresence presence = getOrCreateElementPresence(
        referenceDiff_p.getValue());
    if (presence != null) {
      Role presenceRole = referenceDiff_p.getPresenceRole();
      IMergeableDifference.Editable referenceDiff =
          (IMergeableDifference.Editable)referenceDiff_p;
      // Ref requires value presence, value absence requires no ref
      referenceDiff.markRequires(presence, presenceRole.opposite());
      ((IMergeableDifference.Editable)presence).markRequires(
          referenceDiff_p, presenceRole);
      if (referenceDiff_p.getFeature() != null) {
        // If containment and presence requires ownership, value presence implies ref
        // and no ref implies value absence
        if (referenceDiff_p.getFeature().isContainment() &&
            getMergePolicy().bindPresenceToOwnership(
                _comparison.getScope(presenceRole.opposite()))) {
          ((IMergeableDifference.Editable)presence).markImplies(
              referenceDiff_p, presenceRole.opposite());
          referenceDiff.markImplies(presence, presenceRole);
        } else {
          // Not a containment or no ownership/presence coupling
          EReference opposite = referenceDiff_p.getFeature().getEOpposite();
          // If reference has an eOpposite which is mandatory for addition, then ...
          if (opposite != null && getMergePolicy().isMandatoryForAddition(opposite)) {
            // ... value presence requires ref
            ((IMergeableDifference.Editable)presence).markRequires(
                referenceDiff_p, presenceRole.opposite());
            // ... and no ref requires value absence
            referenceDiff.markRequires(presence, presenceRole);
          }
        }
      }
    }
  }
  
  /**
   * Set dependencies between a reference value presence and the presence of the holder
   * @param referenceDiff_p a non-null reference presence to a partial match
   */
  protected void setPartialReferencingElementDependencies(
      IReferenceValuePresence referenceDiff_p) {
    assert referenceDiff_p.getElementMatch().isPartial();
    IElementPresence presence = getOrCreateElementPresence(
        referenceDiff_p.getElementMatch());
    if (presence != null) {
      Role presenceRole = referenceDiff_p.getPresenceRole();
      // Ref requires element presence, element absence requires no ref
      ((IMergeableDifference.Editable)referenceDiff_p).markRequires(
          presence, presenceRole.opposite());
      ((IMergeableDifference.Editable)presence).markRequires(
          referenceDiff_p, presenceRole);
    }
  }
  
  /**
   * Set the dependencies of a reference value presence
   * @param presence_p a non-null reference value presence
   */
  protected void setReferencedValueDependencies(IReferenceValuePresence presence_p) {
    EReference reference = presence_p.getFeature();
    IMatch valueMatch = presence_p.getValue();
    // Handling dependencies: links (non-container eOpposite)
    if (!reference.isContainment()) {
      IReferenceValuePresence oppositeDiff = presence_p.getOpposite();
      if (oppositeDiff != null)
        setOppositeReferenceDependencies(presence_p, oppositeDiff);
    }
    // Handling dependencies: symmetry (!isMany(), ordering)
    IReferenceValuePresence symmetrical = presence_p.getSymmetrical();
    if (symmetrical != null)
      setSymmetricalValuePresenceDependencies(presence_p, symmetrical);
    // Handling dependencies: presence of element
    if (presence_p.getElementMatch().isPartial())
      setPartialReferencingElementDependencies(presence_p);
    // Handling dependencies: presence of value
    if (valueMatch.isPartial()) {
      setPartialReferencedValueDependencies(presence_p);
    } else if (reference.isContainment()) {
      // Handling dependencies: move of value
      // (implicit global !isMany() to containers which are implicitly symmetric)
      IReferenceValuePresence symmetricalOwnership = presence_p.getSymmetricalOwnership();
      if (symmetricalOwnership != null)
        setSymmetricalOwnershipDependencies(presence_p, symmetricalOwnership);
    }
  }
  
  /**
   * Set dependencies between symmetrical ownership differences
   * @see IReferenceValuePresence#isSymmetricalOwnershipTo(IReferenceValuePresence)
   * @param first_p a non-null reference value presence
   * @param second_p a non-null reference value presence which is the
   *        symmetrical ownership to first_p
   */
  protected void setSymmetricalOwnershipDependencies(
      IReferenceValuePresence first_p, IReferenceValuePresence second_p) {
    assert first_p.isSymmetricalOwnershipTo(second_p);
    IMergeableDifference.Editable first = (IMergeableDifference.Editable)first_p;
    IMergeableDifference.Editable second = (IMergeableDifference.Editable)second_p;
    // Symmetrical ownership presence is implicit on addition...
    first.markImplies(second_p, second_p.getPresenceRole());
    second.markImplies(first_p, first_p.getPresenceRole());
    // ... and explicit on removal
    first.markRequires(second_p, first_p.getPresenceRole());
    second.markRequires(first_p, second_p.getPresenceRole());
  }
  
  /**
   * Set dependencies between symmetrical differences of type value presence
   * @see IValuePresence#isSymmetricalTo(IValuePresence)
   * @param first_p a non-null value presence
   * @param second_p a non-null value presence which is symmetrical to first_p
   */
  protected void setSymmetricalValuePresenceDependencies(
      IValuePresence first_p, IValuePresence second_p) {
    assert first_p.isSymmetricalTo(second_p);
    IMergeableDifference.Editable first = (IMergeableDifference.Editable)first_p;
    IMergeableDifference.Editable second = (IMergeableDifference.Editable)second_p;
    // Symmetrical diffs are implicitly dependent on addition
    first.markImplies(second_p, second_p.getPresenceRole());
    second.markImplies(first_p, first_p.getPresenceRole());
    // Symmetrical diffs are explicitly dependent on removal
    first.markRequires(second_p, first_p.getPresenceRole());
    second.markRequires(first_p, second_p.getPresenceRole());
  }
  
  /**
   * Set the properties which are specific to three-way comparisons to the given
   * difference
   * @param presence_p a non-null attribute value presence
   */
  protected void setThreeWayProperties(IAttributeValuePresence presence_p) {
    EObject ancestorHolder = presence_p.getElementMatch().get(Role.ANCESTOR);
    if (ancestorHolder != null) {
      EAttribute attribute = presence_p.getFeature();
      IFeaturedModelScope ancestorScope = _comparison.getScope(Role.ANCESTOR);
      assert ancestorScope != null; // Thanks to call context
      List<Object> valuesInAncestor = ancestorScope.get(ancestorHolder, attribute);
      boolean aligned;
      if (presence_p.isOrder()) {
        Role presenceRole = presence_p.getPresenceRole();
        List<Object> values = _comparison.getScope(presenceRole).get(
            presence_p.getElementMatch().get(presenceRole),
            presence_p.getFeature());
        int maxIndex = -1;
        aligned = true;
        for (Object value : values) {
          ObjectAndIndex matchingAncestorValue = findEqualAttributeValue(
              attribute, value, valuesInAncestor);
          if (matchingAncestorValue.getObject() != null) {
            if (matchingAncestorValue.getIndex() < maxIndex) {
              // Ordering difference
              aligned = false;
              break;
            }
            maxIndex = matchingAncestorValue.getIndex();
          }
        }
      } else {
        ObjectAndIndex equalInAncestor = findEqualAttributeValue(
            attribute, presence_p.getValue(), valuesInAncestor);
        aligned = equalInAncestor.getObject() != null;
      }
      if (!aligned) {
        // Not aligned with ancestor
        IAttributeValuePresence symmetrical = presence_p.getSymmetrical();
        if (symmetrical != null && !symmetrical.isAlignedWithAncestor()) {
          // Symmetrical is not aligned either: mark both as conflicting
          ((IDifference.Editable)presence_p).markAsConflicting();
          ((IDifference.Editable)symmetrical).markAsConflicting();
        } else {
          // No symmetrical or symmetrical aligned: just mark diff as not aligned
          ((IDifference.Editable)presence_p).markAsDifferentFromAncestor();
        }
      }
    }
  }
  
  /**
   * Set the properties which are specific to three-way comparisons to the given difference
   * @param presence_p a non-null reference value presence
   */
  protected void setThreeWayProperties(IReferenceValuePresence presence_p) {
    EObject ancestorHolder = presence_p.getElementMatch().get(Role.ANCESTOR);
    boolean aligned; // Aligned with ancestor?
    if (ancestorHolder == null) {
      aligned = false;
    } else {
      IMatch valueMatch = presence_p.getValue();
      EObject ancestorValue = valueMatch.get(Role.ANCESTOR); // May be null
      IFeaturedModelScope ancestorScope = _comparison.getScope(Role.ANCESTOR);
      assert ancestorScope != null; // Thanks to call context
      List<EObject> ancestorValues = new FArrayList<EObject>(
          ancestorScope.get(ancestorHolder, presence_p.getFeature()),
          IEqualityTester.BY_REFERENCE);
      if (presence_p.isOrder()) {
        // Order
        Role presenceRole = presence_p.getPresenceRole();
        List<EObject> values = _comparison.getScope(presenceRole).get(
            presence_p.getElementMatch().get(presenceRole),
            presence_p.getFeature());
        int maxIndex = -1;
        aligned = true;
        for (EObject value : values) {
          IMatch currentValueMatch = getMapping().getMatchFor(value, presenceRole);
          if (currentValueMatch != null) {
            EObject matchAncestor = currentValueMatch.get(Role.ANCESTOR);
            if (matchAncestor != null) {
              int index = ancestorValues.indexOf(matchAncestor);
              if (index >= 0) {
                if (index < maxIndex) {
                  // Ordering difference
                  aligned = false;
                  break;
                }
                maxIndex = index;
              }
            }
          }
        }
      } else {
        // Not an order
        aligned = ancestorValues.contains(ancestorValue);
      }
    }
    if (!aligned) {
      // Not aligned with ancestor
      IReferenceValuePresence symmetrical = presence_p.getSymmetrical();
      if (symmetrical != null && !symmetrical.isAlignedWithAncestor()) {
        // Symmetrical is not aligned either: mark both as conflicting
        ((IDifference.Editable)presence_p).markAsConflicting();
        ((IDifference.Editable)symmetrical).markAsConflicting();
      } else {
        // No symmetrical or symmetrical aligned: just mark diff as not aligned
        ((IDifference.Editable)presence_p).markAsDifferentFromAncestor();
      }
    }
  }
  
  
  /**
   * A trivial data structure that associates an object and an index.
   * Either the object is not null and the index is greater than or equal to 0,
   * or the structure is (null, -1).
   */
  protected static class ObjectAndIndex {
    /** The potentially null object */
    private Object _object;
    /** The index ranging from -1 to max int, inclusive */
    private int _index;
    /**
     * Full constructor
     * @param object_p a non-null object
     * @param index_p a positive int or 0
     */
    public ObjectAndIndex(Object object_p, int index_p) {
      assert object_p != null && index_p >= 0;
      _object = object_p;
      _index = index_p;
    }
    /**
     * Constructor for no object and -1 as index
     */
    public ObjectAndIndex() {
      _object = null;
      _index = -1;
    }
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object peer_p) {
      boolean result = false;
      if (peer_p instanceof ObjectAndIndex) {
        ObjectAndIndex peer = (ObjectAndIndex)peer_p;
        result = _object == null && peer.getObject() == null ||
          _object != null && _object.equals(peer.getObject());
        result = result && _index == peer.getIndex();
      }
      return result;
    }
    /**
     * Return the object
     * @return a potentially null object
     */
    public Object getObject() {
      return _object;
    }
    /**
     * Return the index
     * @return an int ranging from -1 to max int, inclusive
     */
    public int getIndex() {
      return _index;
    }
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      return (_object != null? _object.hashCode(): 0) +
        Integer.valueOf(_index).hashCode();
    }
  }
  
}
