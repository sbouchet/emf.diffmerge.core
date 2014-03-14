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
package org.eclipse.emf.diffmerge.diffdata.impl;

import java.util.Map;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.diffmerge.api.IDiffPolicy;
import org.eclipse.emf.diffmerge.api.IMatch;
import org.eclipse.emf.diffmerge.api.IMatchPolicy;
import org.eclipse.emf.diffmerge.api.IMergePolicy;
import org.eclipse.emf.diffmerge.api.Role;
import org.eclipse.emf.diffmerge.api.diff.IAttributeValuePresence;
import org.eclipse.emf.diffmerge.api.diff.IReferenceValuePresence;
import org.eclipse.emf.diffmerge.api.scopes.IEditableModelScope;
import org.eclipse.emf.diffmerge.diffdata.DiffdataFactory;
import org.eclipse.emf.diffmerge.diffdata.DiffdataPackage;
import org.eclipse.emf.diffmerge.diffdata.EAttributeValuePresence;
import org.eclipse.emf.diffmerge.diffdata.EComparison;
import org.eclipse.emf.diffmerge.diffdata.EElementPresence;
import org.eclipse.emf.diffmerge.diffdata.EMapping;
import org.eclipse.emf.diffmerge.diffdata.EMatch;
import org.eclipse.emf.diffmerge.diffdata.EReferenceValuePresence;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class DiffdataFactoryImpl extends EFactoryImpl implements
    DiffdataFactory {
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static DiffdataFactory init() {
    try {
      DiffdataFactory theDiffdataFactory = (DiffdataFactory) EPackage.Registry.INSTANCE
          .getEFactory(DiffdataPackage.eNS_URI);
      if (theDiffdataFactory != null) {
        return theDiffdataFactory;
      }
    } catch (Exception exception) {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new DiffdataFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DiffdataFactoryImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass) {
    switch (eClass.getClassifierID()) {
    case DiffdataPackage.ECOMPARISON:
      return createEComparison();
    case DiffdataPackage.EMAPPING:
      return createEMapping();
    case DiffdataPackage.EMATCH:
      return createEMatch();
    case DiffdataPackage.EELEMENT_PRESENCE:
      return createEElementPresence();
    case DiffdataPackage.EATTRIBUTE_VALUE_PRESENCE:
      return createEAttributeValuePresence();
    case DiffdataPackage.EREFERENCE_VALUE_PRESENCE:
      return createEReferenceValuePresence();
    case DiffdataPackage.ATTRIBUTE_TO_VALUE_TO_DIFFERENCE_ENTRY:
      return (EObject) createAttributeToValueToDifferenceEntry();
    case DiffdataPackage.VALUE_TO_DIFFERENCE_ENTRY:
      return (EObject) createValueToDifferenceEntry();
    case DiffdataPackage.REFERENCE_TO_MATCH_TO_DIFFERENCE_ENTRY:
      return (EObject) createReferenceToMatchToDifferenceEntry();
    case DiffdataPackage.MATCH_TO_DIFFERENCE_ENTRY:
      return (EObject) createMatchToDifferenceEntry();
    default:
      throw new IllegalArgumentException(
          "The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue) {
    switch (eDataType.getClassifierID()) {
    case DiffdataPackage.IEDITABLE_MODEL_SCOPE:
      return createIEditableModelScopeFromString(eDataType, initialValue);
    case DiffdataPackage.IMATCH_POLICY:
      return createIMatchPolicyFromString(eDataType, initialValue);
    case DiffdataPackage.IDIFF_POLICY:
      return createIDiffPolicyFromString(eDataType, initialValue);
    case DiffdataPackage.IMERGE_POLICY:
      return createIMergePolicyFromString(eDataType, initialValue);
    case DiffdataPackage.ROLE:
      return createRoleFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException(
          "The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue) {
    switch (eDataType.getClassifierID()) {
    case DiffdataPackage.IEDITABLE_MODEL_SCOPE:
      return convertIEditableModelScopeToString(eDataType, instanceValue);
    case DiffdataPackage.IMATCH_POLICY:
      return convertIMatchPolicyToString(eDataType, instanceValue);
    case DiffdataPackage.IDIFF_POLICY:
      return convertIDiffPolicyToString(eDataType, instanceValue);
    case DiffdataPackage.IMERGE_POLICY:
      return convertIMergePolicyToString(eDataType, instanceValue);
    case DiffdataPackage.ROLE:
      return convertRoleToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException(
          "The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EComparison createEComparison() {
    EComparisonImpl eComparison = new EComparisonImpl();
    return eComparison;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EMapping createEMapping() {
    EMappingImpl eMapping = new EMappingImpl();
    return eMapping;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EMatch createEMatch() {
    EMatchImpl eMatch = new EMatchImpl();
    return eMatch;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EElementPresence createEElementPresence() {
    EElementPresenceImpl eElementPresence = new EElementPresenceImpl();
    return eElementPresence;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttributeValuePresence createEAttributeValuePresence() {
    EAttributeValuePresenceImpl eAttributeValuePresence = new EAttributeValuePresenceImpl();
    return eAttributeValuePresence;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReferenceValuePresence createEReferenceValuePresence() {
    EReferenceValuePresenceImpl eReferenceValuePresence = new EReferenceValuePresenceImpl();
    return eReferenceValuePresence;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<EAttribute, EMap<Object, IAttributeValuePresence>> createAttributeToValueToDifferenceEntry() {
    AttributeToValueToDifferenceEntryImpl attributeToValueToDifferenceEntry = new AttributeToValueToDifferenceEntryImpl();
    return attributeToValueToDifferenceEntry;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<Object, IAttributeValuePresence> createValueToDifferenceEntry() {
    ValueToDifferenceEntryImpl valueToDifferenceEntry = new ValueToDifferenceEntryImpl();
    return valueToDifferenceEntry;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<EReference, EMap<IMatch, IReferenceValuePresence>> createReferenceToMatchToDifferenceEntry() {
    ReferenceToMatchToDifferenceEntryImpl referenceToMatchToDifferenceEntry = new ReferenceToMatchToDifferenceEntryImpl();
    return referenceToMatchToDifferenceEntry;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Map.Entry<IMatch, IReferenceValuePresence> createMatchToDifferenceEntry() {
    MatchToDifferenceEntryImpl matchToDifferenceEntry = new MatchToDifferenceEntryImpl();
    return matchToDifferenceEntry;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IEditableModelScope createIEditableModelScopeFromString(
      EDataType eDataType, String initialValue) {
    return (IEditableModelScope) super
        .createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertIEditableModelScopeToString(EDataType eDataType,
      Object instanceValue) {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IMatchPolicy createIMatchPolicyFromString(EDataType eDataType,
      String initialValue) {
    return (IMatchPolicy) super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertIMatchPolicyToString(EDataType eDataType,
      Object instanceValue) {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IDiffPolicy createIDiffPolicyFromString(EDataType eDataType,
      String initialValue) {
    return (IDiffPolicy) super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertIDiffPolicyToString(EDataType eDataType,
      Object instanceValue) {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IMergePolicy createIMergePolicyFromString(EDataType eDataType,
      String initialValue) {
    return (IMergePolicy) super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertIMergePolicyToString(EDataType eDataType,
      Object instanceValue) {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Role createRoleFromString(EDataType eDataType, String initialValue) {
    return (Role) super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertRoleToString(EDataType eDataType, Object instanceValue) {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DiffdataPackage getDiffdataPackage() {
    return (DiffdataPackage) getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static DiffdataPackage getPackage() {
    return DiffdataPackage.eINSTANCE;
  }

} //DiffdataFactoryImpl
