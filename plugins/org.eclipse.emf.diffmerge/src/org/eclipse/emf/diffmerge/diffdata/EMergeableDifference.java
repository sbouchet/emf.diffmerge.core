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
package org.eclipse.emf.diffmerge.diffdata;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.diffmerge.api.Role;
import org.eclipse.emf.diffmerge.api.diff.IMergeableDifference;
import org.eclipse.emf.diffmerge.api.diff.IMergeableDifference.Editable;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EMergeable Difference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.diffmerge.diffdata.EMergeableDifference#getComparison <em>Comparison</em>}</li>
 *   <li>{@link org.eclipse.emf.diffmerge.diffdata.EMergeableDifference#isAlignedWithAncestor <em>Aligned With Ancestor</em>}</li>
 *   <li>{@link org.eclipse.emf.diffmerge.diffdata.EMergeableDifference#isConflicting <em>Conflicting</em>}</li>
 *   <li>{@link org.eclipse.emf.diffmerge.diffdata.EMergeableDifference#getMergeDestination <em>Merge Destination</em>}</li>
 *   <li>{@link org.eclipse.emf.diffmerge.diffdata.EMergeableDifference#getPossibleMergeDestinations <em>Possible Merge Destinations</em>}</li>
 *   <li>{@link org.eclipse.emf.diffmerge.diffdata.EMergeableDifference#getExplicitDependenciesForTarget <em>Explicit Dependencies For Target</em>}</li>
 *   <li>{@link org.eclipse.emf.diffmerge.diffdata.EMergeableDifference#getExplicitDependenciesForReference <em>Explicit Dependencies For Reference</em>}</li>
 *   <li>{@link org.eclipse.emf.diffmerge.diffdata.EMergeableDifference#getImplicitDependenciesForTarget <em>Implicit Dependencies For Target</em>}</li>
 *   <li>{@link org.eclipse.emf.diffmerge.diffdata.EMergeableDifference#getImplicitDependenciesForReference <em>Implicit Dependencies For Reference</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.diffmerge.diffdata.DiffdataPackage#getEMergeableDifference()
 * @model abstract="true" superTypes="org.eclipse.emf.diffmerge.diffdata.IEditableMergeableDifference"
 * @generated
 */
public interface EMergeableDifference extends EObject, Editable {
  /**
   * Returns the value of the '<em><b>Comparison</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Comparison</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Comparison</em>' reference.
   * @see org.eclipse.emf.diffmerge.diffdata.DiffdataPackage#getEMergeableDifference_Comparison()
   * @model required="true" changeable="false"
   * @generated
   */
  EComparison getComparison();

  /**
   * Returns the value of the '<em><b>Aligned With Ancestor</b></em>' attribute.
   * The default value is <code>"true"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Aligned With Ancestor</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Aligned With Ancestor</em>' attribute.
   * @see #setAlignedWithAncestor(boolean)
   * @see org.eclipse.emf.diffmerge.diffdata.DiffdataPackage#getEMergeableDifference_AlignedWithAncestor()
   * @model default="true" required="true"
   * @generated
   */
  boolean isAlignedWithAncestor();

  /**
   * Sets the value of the '{@link org.eclipse.emf.diffmerge.diffdata.EMergeableDifference#isAlignedWithAncestor <em>Aligned With Ancestor</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Aligned With Ancestor</em>' attribute.
   * @see #isAlignedWithAncestor()
   * @generated
   */
  void setAlignedWithAncestor(boolean value);

  /**
   * Returns the value of the '<em><b>Conflicting</b></em>' attribute.
   * The default value is <code>"false"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Conflicting</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Conflicting</em>' attribute.
   * @see #setConflicting(boolean)
   * @see org.eclipse.emf.diffmerge.diffdata.DiffdataPackage#getEMergeableDifference_Conflicting()
   * @model default="false" required="true"
   * @generated
   */
  boolean isConflicting();

  /**
   * Sets the value of the '{@link org.eclipse.emf.diffmerge.diffdata.EMergeableDifference#isConflicting <em>Conflicting</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Conflicting</em>' attribute.
   * @see #isConflicting()
   * @generated
   */
  void setConflicting(boolean value);

  /**
   * Returns the value of the '<em><b>Merge Destination</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Merge Destination</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Merge Destination</em>' attribute.
   * @see #setMergeDestination(Role)
   * @see org.eclipse.emf.diffmerge.diffdata.DiffdataPackage#getEMergeableDifference_MergeDestination()
   * @model dataType="org.eclipse.emf.diffmerge.diffdata.Role"
   * @generated
   */
  Role getMergeDestination();

  /**
   * Sets the value of the '{@link org.eclipse.emf.diffmerge.diffdata.EMergeableDifference#getMergeDestination <em>Merge Destination</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Merge Destination</em>' attribute.
   * @see #getMergeDestination()
   * @generated
   */
  void setMergeDestination(Role value);

  /**
   * Returns the value of the '<em><b>Possible Merge Destinations</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.emf.diffmerge.api.Role}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Possible Merge Destinations</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Possible Merge Destinations</em>' attribute list.
   * @see org.eclipse.emf.diffmerge.diffdata.DiffdataPackage#getEMergeableDifference_PossibleMergeDestinations()
   * @model dataType="org.eclipse.emf.diffmerge.diffdata.Role"
   * @generated
   */
  EList<Role> getPossibleMergeDestinations();

  /**
   * Returns the value of the '<em><b>Explicit Dependencies For Target</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.diffmerge.api.diff.IMergeableDifference}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Explicit Dependencies For Target</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Explicit Dependencies For Target</em>' reference list.
   * @see org.eclipse.emf.diffmerge.diffdata.DiffdataPackage#getEMergeableDifference_ExplicitDependenciesForTarget()
   * @model type="org.eclipse.emf.diffmerge.diffdata.IMergeableDifference"
   * @generated
   */
  EList<IMergeableDifference> getExplicitDependenciesForTarget();

  /**
   * Returns the value of the '<em><b>Explicit Dependencies For Reference</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.diffmerge.api.diff.IMergeableDifference}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Explicit Dependencies For Reference</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Explicit Dependencies For Reference</em>' reference list.
   * @see org.eclipse.emf.diffmerge.diffdata.DiffdataPackage#getEMergeableDifference_ExplicitDependenciesForReference()
   * @model type="org.eclipse.emf.diffmerge.diffdata.IMergeableDifference"
   * @generated
   */
  EList<IMergeableDifference> getExplicitDependenciesForReference();

  /**
   * Returns the value of the '<em><b>Implicit Dependencies For Target</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.diffmerge.api.diff.IMergeableDifference}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Implicit Dependencies For Target</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Implicit Dependencies For Target</em>' reference list.
   * @see org.eclipse.emf.diffmerge.diffdata.DiffdataPackage#getEMergeableDifference_ImplicitDependenciesForTarget()
   * @model type="org.eclipse.emf.diffmerge.diffdata.IMergeableDifference"
   * @generated
   */
  EList<IMergeableDifference> getImplicitDependenciesForTarget();

  /**
   * Returns the value of the '<em><b>Implicit Dependencies For Reference</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.diffmerge.api.diff.IMergeableDifference}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Implicit Dependencies For Reference</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Implicit Dependencies For Reference</em>' reference list.
   * @see org.eclipse.emf.diffmerge.diffdata.DiffdataPackage#getEMergeableDifference_ImplicitDependenciesForReference()
   * @model type="org.eclipse.emf.diffmerge.diffdata.IMergeableDifference"
   * @generated
   */
  EList<IMergeableDifference> getImplicitDependenciesForReference();

} // EMergeableDifference
