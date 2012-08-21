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
package org.eclipse.emf.diffmerge.api;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.diffmerge.api.diff.IAttributeValuePresence;
import org.eclipse.emf.diffmerge.api.diff.IDifference;
import org.eclipse.emf.diffmerge.api.diff.IElementPresence;
import org.eclipse.emf.diffmerge.api.diff.IReferenceValuePresence;
import org.eclipse.emf.diffmerge.api.scopes.IFeaturedModelScope;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;



/**
 * A comparison between model scopes.
 * A comparison, once defined, can be computed using match, diff and merge policies.
 * After computation, it provides a mapping between the contents of the scopes,
 * a set of differences based on this mapping and the ability to merge a subset of
 * those differences.
 * @author Olivier Constant
 */
public interface IComparison {
  
  /**
   * Clear this comparison, going back to its state before computation
   */
  void clear();
  
  /**
   * Compute this comparison according to the given policies
   * Postcondition: result.isOk() => getLastMergePolicy() != null
   * @param matchPolicy_p an optional match policy (null stands for default)
   * @param diffPolicy_p an optional diff policy (null stands for default)
   * @param mergePolicy_p an optional merge policy (null stands for default)
   * @param monitor_p an optional progress monitor
   * @return a non-null status of the execution
   */
  IStatus compute(IMatchPolicy matchPolicy_p, IDiffPolicy diffPolicy_p,
      IMergePolicy mergePolicy_p, IProgressMonitor monitor_p);
  
  /**
   * Return a tree iterator over matches based on getContentsOf(IMatch, Role).
   * @param role_p a non-null role
   * @return a non-null iterator
   */
  TreeIterator<IMatch> getAllContents(Role role_p);
  
  /**
   * Return the match for the container of the given match in the given role.
   * Result is null iff there is no container in the corresponding scope or
   * no element in the role for the given match.
   * @param match_p a non-null match
   * @param role_p a non-null role
   * @return a potentially null match
   */
  IMatch getContainerOf(IMatch match_p, Role role_p);
  
  /**
   * Return the matches for the roots of the TARGET and REFERENCE scopes
   * @return a non-null, potentially empty, unmodifiable ordered set of matches
   */
  List<IMatch> getContents();
  
  /**
   * Return the matches for the roots of the scope of the given role
   * @param role_p a non-null role
   * @return a non-null, potentially empty, unmodifiable ordered set of matches
   */
  List<IMatch> getContents(Role role_p);
  
  /**
   * Return the matches for the contents of the given match in the TARGET and
   * REFERENCE roles. Matches from REFERENCE come first.
   * @param match_p a non-null match
   * @return a non-null, potentially empty, unmodifiable ordered set of matches
   */
  List<IMatch> getContentsOf(IMatch match_p);
  
  /**
   * Return the matches for the contents of the given match in the given role
   * @param match_p a non-null match
   * @param role_p a non-null role
   * @return a non-null, potentially empty, unmodifiable ordered set of matches
   */
  List<IMatch> getContentsOf(IMatch match_p, Role role_p);
  
  /**
   * Return all differences in the given role.
   * This operation cannot be assumed to be efficient.
   * The resulting collection may become obsolete if the comparison is reset.
   * @param role_p a role which is TARGET or REFERENCE
   * @return a non-null, unmodifiable list which may contain duplicates if differences
   *         are not low-level, technical differences
   */
  List<IDifference> getDifferences(Role role_p);
  
  /**
   * Return the last diff policy used by this comparison
   * @return a possibly null diff policy (non-null if the last compute(...) succeeded)
   */
  IDiffPolicy getLastDiffPolicy();
  
  /**
   * Return the last match policy used by this comparison
   * @return a possibly null match policy (non-null if the last compute(...) succeeded)
   */
  IMatchPolicy getLastMatchPolicy();
  
  /**
   * Return the last merge policy used by this comparison
   * @return a possibly null merge policy (non-null if the last compute(...) succeeded)
   */
  IMergePolicy getLastMergePolicy();
  
  /**
   * Return the mapping between the model scopes of this comparison
   * @return a non-null mapping
   */
  IMapping getMapping();
  
  /**
   * Return the number of differences as the sum of the number of differences
   * per mapping entry
   */
  int getNbDifferences();
  
  /**
   * Return the number of differences which are not related to the containment
   * tree (on attributes and cross references)
   * Class invariant: getNbProperElementDifferences() < getNbDifferences()
   */
  int getNbProperElementDifferences();
  
  /**
   * Return the set of differences which have not been merged.
   * The resulting collection may become obsolete if the comparison is reset.
   * @return a non-null, potentially empty, unmodifiable collection
   */
  Collection<IDifference> getRemainingDifferences();
  
  /**
   * Return the scope for the given role
   * @param role_p a non-null role
   * @return a scope which is non-null iff the given role is covered by this comparison
   */
  IFeaturedModelScope getScope(Role role_p);
  
  /**
   * Return whether there are differences which have not been merged.
   * Class invariant: hasRemainingDifferences() == !getRemainingDifferences().isEmpty()
   * @return a non-null, potentially empty, unmodifiable collection
   */
  boolean hasRemainingDifferences();
  
  /**
   * Return whether every element in the scope of the given role is covered
   * by the mapping of the matching phase
   * @param role_p a non-null comparison role
   */
  boolean isCompleteFor(Role role_p);
  
  /**
   * Return whether this comparison is three-way and not two-way, that is,
   * whether it has an ancestor scope which allows detecting conflicts
   * between the target and reference scopes
   */
  boolean isThreeWay();
  
  /**
   * Merge the remaining differences to the given destination role
   * @param destination_p a role which is TARGET or REFERENCE
   * @param updateReferences_p whether references of the added elements must be set
   * @param monitor_p an optional progress monitor (null for no progress monitoring)
   * @return a non-null, potentially empty, unmodifiable set of the differences
   *         which have actually been merged
   */
  Collection<IDifference> merge(Role destination_p, boolean updateReferences_p,
      IProgressMonitor monitor_p);
  
  /**
   * Merge the given set of differences to the given destination role
   * @param differences_p a non-null, potentially empty set of differences
   * @param destination_p a role which is TARGET or REFERENCE
   * @param updateReferences_p whether references of the added elements must be set
   * @param monitor_p an optional progress monitor (null for no progress monitoring)
   * @return a non-null, potentially empty, unmodifiable set of the differences
   *         which have actually been merged
   */
  Collection<IDifference> merge(Collection<? extends IDifference> differences_p, Role destination_p,
      boolean updateReferences_p, IProgressMonitor monitor_p);
  
  /**
   * Apply the given merger to the comparison
   * @param merger_p a non-null merger
   * @param updateReferences_p whether references of the added elements must be set
   * @param monitor_p an optional progress monitor (null for no progress monitoring)
   * @return a non-null, potentially empty, unmodifiable set of the differences
   *         which have actually been merged
   */
  Collection<IDifference> merge(IMergeSelector merger_p, boolean updateReferences_p,
      IProgressMonitor monitor_p);
  
  
  /**
   * A comparison with editing features.
   * All concrete classes implementing IComparison must also implement this interface.
   */
  public static interface Editable extends IComparison {
    /**
     * Create and return an attribute value presence with the given characteristics
     * @param elementMatch_p the non-null match for the element holding the value
     * @param attribute_p the non-null attribute holding the value
     * @param value_p the non-null value held
     * @param presenceRole_p the role in which the value is held: TARGET or REFERENCE
     * @param isOrder_p whether the value presence is solely due to ordering
     * @return a non-null attribute value presence
     */
    IAttributeValuePresence newAttributeValuePresence(
        IMatch elementMatch_p, EAttribute attribute_p, Object value_p,
        Role presenceRole_p, boolean isOrder_p);
    
    /**
     * Create and return a reference value presence with the given characteristics
     * @param elementMatch_p the non-null partial match for the element presence
     * @param ownerMatch_p a potentially null match for the owner of the element
     * @return a non-null element presence
     */
    IElementPresence newElementPresence(IMatch elementMatch_p, IMatch ownerMatch_p);
    
    /**
     * Create and return a match with the given characteristics
     * @param targetElement_p an optional element on the TARGET side
     * @param referenceElement_p an optional element on the REFERENCE side
     * @param ancestorElement_p an optional element on the ANCESTOR side
     * @return a non-null match
     */
    IMatch newMatch(EObject targetElement_p, EObject referenceElement_p,
        EObject ancestorElement_p);
    
    /**
     * Create and return a reference value presence with the given characteristics
     * @param elementMatch_p the non-null match for the element holding the value
     * @param reference_p the non-null reference holding the value
     * @param valueMatch_p the non-null match corresponding to the value held
     * @param presenceRole_p the role in which the value is held: TARGET or REFERENCE
     * @param isOrder_p whether the value presence is solely due to ordering
     * @return a non-null reference value presence
     */
    IReferenceValuePresence newReferenceValuePresence(
        IMatch elementMatch_p, EReference reference_p, IMatch valueMatch_p,
        Role presenceRole_p, boolean isOrder_p);
  }
  
}