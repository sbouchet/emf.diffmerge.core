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
package org.eclipse.emf.diffmerge.diffdata.util;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.diffmerge.api.IComparison;
import org.eclipse.emf.diffmerge.api.IComparison.Editable;
import org.eclipse.emf.diffmerge.api.IMapping;
import org.eclipse.emf.diffmerge.api.IMatch;
import org.eclipse.emf.diffmerge.api.diff.IAttributeValuePresence;
import org.eclipse.emf.diffmerge.api.diff.IElementPresence;
import org.eclipse.emf.diffmerge.api.diff.IElementRelativePresence;
import org.eclipse.emf.diffmerge.api.diff.IMergeableDifference;
import org.eclipse.emf.diffmerge.api.diff.IReferenceValuePresence;
import org.eclipse.emf.diffmerge.api.diff.IValuePresence;
import org.eclipse.emf.diffmerge.diffdata.DiffdataPackage;
import org.eclipse.emf.diffmerge.diffdata.EAttributeValuePresence;
import org.eclipse.emf.diffmerge.diffdata.EComparison;
import org.eclipse.emf.diffmerge.diffdata.EElementPresence;
import org.eclipse.emf.diffmerge.diffdata.EElementRelativePresence;
import org.eclipse.emf.diffmerge.diffdata.EMapping;
import org.eclipse.emf.diffmerge.diffdata.EMatch;
import org.eclipse.emf.diffmerge.diffdata.EMergeableDifference;
import org.eclipse.emf.diffmerge.diffdata.EReferenceValuePresence;
import org.eclipse.emf.diffmerge.diffdata.EValuePresence;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.diffmerge.diffdata.DiffdataPackage
 * @generated
 */
public class DiffdataSwitch<T> {
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static DiffdataPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DiffdataSwitch() {
    if (modelPackage == null) {
      modelPackage = DiffdataPackage.eINSTANCE;
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  public T doSwitch(EObject theEObject) {
    return doSwitch(theEObject.eClass(), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(EClass theEClass, EObject theEObject) {
    if (theEClass.eContainer() == modelPackage) {
      return doSwitch(theEClass.getClassifierID(), theEObject);
    } else {
      List<EClass> eSuperTypes = theEClass.getESuperTypes();
      return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(
          eSuperTypes.get(0), theEObject);
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(int classifierID, EObject theEObject) {
    switch (classifierID) {
    case DiffdataPackage.ECOMPARISON: {
      EComparison eComparison = (EComparison) theEObject;
      T result = caseEComparison(eComparison);
      if (result == null)
        result = caseIEditableComparison(eComparison);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.EMAPPING: {
      EMapping eMapping = (EMapping) theEObject;
      T result = caseEMapping(eMapping);
      if (result == null)
        result = caseIEditableMapping(eMapping);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.EMATCH: {
      EMatch eMatch = (EMatch) theEObject;
      T result = caseEMatch(eMatch);
      if (result == null)
        result = caseIEditableMatch(eMatch);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.EMERGEABLE_DIFFERENCE: {
      EMergeableDifference eMergeableDifference = (EMergeableDifference) theEObject;
      T result = caseEMergeableDifference(eMergeableDifference);
      if (result == null)
        result = caseIEditableMergeableDifference(eMergeableDifference);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.EELEMENT_RELATIVE_PRESENCE: {
      EElementRelativePresence eElementRelativePresence = (EElementRelativePresence) theEObject;
      T result = caseEElementRelativePresence(eElementRelativePresence);
      if (result == null)
        result = caseEMergeableDifference(eElementRelativePresence);
      if (result == null)
        result = caseIElementRelativePresence(eElementRelativePresence);
      if (result == null)
        result = caseIEditableMergeableDifference(eElementRelativePresence);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.EELEMENT_PRESENCE: {
      EElementPresence eElementPresence = (EElementPresence) theEObject;
      T result = caseEElementPresence(eElementPresence);
      if (result == null)
        result = caseEElementRelativePresence(eElementPresence);
      if (result == null)
        result = caseIElementPresence(eElementPresence);
      if (result == null)
        result = caseEMergeableDifference(eElementPresence);
      if (result == null)
        result = caseIElementRelativePresence(eElementPresence);
      if (result == null)
        result = caseIEditableMergeableDifference(eElementPresence);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.EVALUE_PRESENCE: {
      EValuePresence eValuePresence = (EValuePresence) theEObject;
      T result = caseEValuePresence(eValuePresence);
      if (result == null)
        result = caseEElementRelativePresence(eValuePresence);
      if (result == null)
        result = caseIValuePresence(eValuePresence);
      if (result == null)
        result = caseEMergeableDifference(eValuePresence);
      if (result == null)
        result = caseIElementRelativePresence(eValuePresence);
      if (result == null)
        result = caseIEditableMergeableDifference(eValuePresence);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.EATTRIBUTE_VALUE_PRESENCE: {
      EAttributeValuePresence eAttributeValuePresence = (EAttributeValuePresence) theEObject;
      T result = caseEAttributeValuePresence(eAttributeValuePresence);
      if (result == null)
        result = caseEValuePresence(eAttributeValuePresence);
      if (result == null)
        result = caseIAttributeValuePresence(eAttributeValuePresence);
      if (result == null)
        result = caseEElementRelativePresence(eAttributeValuePresence);
      if (result == null)
        result = caseIValuePresence(eAttributeValuePresence);
      if (result == null)
        result = caseEMergeableDifference(eAttributeValuePresence);
      if (result == null)
        result = caseIElementRelativePresence(eAttributeValuePresence);
      if (result == null)
        result = caseIEditableMergeableDifference(eAttributeValuePresence);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.EREFERENCE_VALUE_PRESENCE: {
      EReferenceValuePresence eReferenceValuePresence = (EReferenceValuePresence) theEObject;
      T result = caseEReferenceValuePresence(eReferenceValuePresence);
      if (result == null)
        result = caseEValuePresence(eReferenceValuePresence);
      if (result == null)
        result = caseIReferenceValuePresence(eReferenceValuePresence);
      if (result == null)
        result = caseEElementRelativePresence(eReferenceValuePresence);
      if (result == null)
        result = caseIValuePresence(eReferenceValuePresence);
      if (result == null)
        result = caseEMergeableDifference(eReferenceValuePresence);
      if (result == null)
        result = caseIElementRelativePresence(eReferenceValuePresence);
      if (result == null)
        result = caseIEditableMergeableDifference(eReferenceValuePresence);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.ATTRIBUTE_TO_VALUE_TO_DIFFERENCE_ENTRY: {
      @SuppressWarnings("unchecked")
      Map.Entry<EAttribute, EMap<Object, IAttributeValuePresence>> attributeToValueToDifferenceEntry = (Map.Entry<EAttribute, EMap<Object, IAttributeValuePresence>>) theEObject;
      T result = caseAttributeToValueToDifferenceEntry(attributeToValueToDifferenceEntry);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.VALUE_TO_DIFFERENCE_ENTRY: {
      @SuppressWarnings("unchecked")
      Map.Entry<Object, IAttributeValuePresence> valueToDifferenceEntry = (Map.Entry<Object, IAttributeValuePresence>) theEObject;
      T result = caseValueToDifferenceEntry(valueToDifferenceEntry);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.REFERENCE_TO_MATCH_TO_DIFFERENCE_ENTRY: {
      @SuppressWarnings("unchecked")
      Map.Entry<EReference, EMap<IMatch, IReferenceValuePresence>> referenceToMatchToDifferenceEntry = (Map.Entry<EReference, EMap<IMatch, IReferenceValuePresence>>) theEObject;
      T result = caseReferenceToMatchToDifferenceEntry(referenceToMatchToDifferenceEntry);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.MATCH_TO_DIFFERENCE_ENTRY: {
      @SuppressWarnings("unchecked")
      Map.Entry<IMatch, IReferenceValuePresence> matchToDifferenceEntry = (Map.Entry<IMatch, IReferenceValuePresence>) theEObject;
      T result = caseMatchToDifferenceEntry(matchToDifferenceEntry);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.ICOMPARISON: {
      IComparison iComparison = (IComparison) theEObject;
      T result = caseIComparison(iComparison);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.IEDITABLE_COMPARISON: {
      Editable iEditableComparison = (Editable) theEObject;
      T result = caseIEditableComparison(iEditableComparison);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.IMAPPING: {
      IMapping iMapping = (IMapping) theEObject;
      T result = caseIMapping(iMapping);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.IEDITABLE_MAPPING: {
      org.eclipse.emf.diffmerge.api.IMapping.Editable iEditableMapping = (org.eclipse.emf.diffmerge.api.IMapping.Editable) theEObject;
      T result = caseIEditableMapping(iEditableMapping);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.IMATCH: {
      IMatch iMatch = (IMatch) theEObject;
      T result = caseIMatch(iMatch);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.IEDITABLE_MATCH: {
      org.eclipse.emf.diffmerge.api.IMatch.Editable iEditableMatch = (org.eclipse.emf.diffmerge.api.IMatch.Editable) theEObject;
      T result = caseIEditableMatch(iEditableMatch);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.IMERGEABLE_DIFFERENCE: {
      IMergeableDifference iMergeableDifference = (IMergeableDifference) theEObject;
      T result = caseIMergeableDifference(iMergeableDifference);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.IEDITABLE_MERGEABLE_DIFFERENCE: {
      org.eclipse.emf.diffmerge.api.diff.IMergeableDifference.Editable iEditableMergeableDifference = (org.eclipse.emf.diffmerge.api.diff.IMergeableDifference.Editable) theEObject;
      T result = caseIEditableMergeableDifference(iEditableMergeableDifference);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.IELEMENT_RELATIVE_PRESENCE: {
      IElementRelativePresence iElementRelativePresence = (IElementRelativePresence) theEObject;
      T result = caseIElementRelativePresence(iElementRelativePresence);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.IELEMENT_PRESENCE: {
      IElementPresence iElementPresence = (IElementPresence) theEObject;
      T result = caseIElementPresence(iElementPresence);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.IVALUE_PRESENCE: {
      IValuePresence iValuePresence = (IValuePresence) theEObject;
      T result = caseIValuePresence(iValuePresence);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.IATTRIBUTE_VALUE_PRESENCE: {
      IAttributeValuePresence iAttributeValuePresence = (IAttributeValuePresence) theEObject;
      T result = caseIAttributeValuePresence(iAttributeValuePresence);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    case DiffdataPackage.IREFERENCE_VALUE_PRESENCE: {
      IReferenceValuePresence iReferenceValuePresence = (IReferenceValuePresence) theEObject;
      T result = caseIReferenceValuePresence(iReferenceValuePresence);
      if (result == null)
        result = defaultCase(theEObject);
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EComparison</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EComparison</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEComparison(EComparison object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EMapping</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EMapping</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEMapping(EMapping object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EMatch</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EMatch</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEMatch(EMatch object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EMergeable Difference</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EMergeable Difference</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEMergeableDifference(EMergeableDifference object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EElement Relative Presence</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EElement Relative Presence</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEElementRelativePresence(EElementRelativePresence object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EElement Presence</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EElement Presence</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEElementPresence(EElementPresence object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EValue Presence</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EValue Presence</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEValuePresence(EValuePresence object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EAttribute Value Presence</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EAttribute Value Presence</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEAttributeValuePresence(EAttributeValuePresence object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EReference Value Presence</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EReference Value Presence</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseEReferenceValuePresence(EReferenceValuePresence object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Attribute To Value To Difference Entry</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Attribute To Value To Difference Entry</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAttributeToValueToDifferenceEntry(
      Map.Entry<EAttribute, EMap<Object, IAttributeValuePresence>> object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Value To Difference Entry</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Value To Difference Entry</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseValueToDifferenceEntry(
      Map.Entry<Object, IAttributeValuePresence> object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Reference To Match To Difference Entry</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Reference To Match To Difference Entry</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseReferenceToMatchToDifferenceEntry(
      Map.Entry<EReference, EMap<IMatch, IReferenceValuePresence>> object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Match To Difference Entry</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Match To Difference Entry</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMatchToDifferenceEntry(
      Map.Entry<IMatch, IReferenceValuePresence> object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IComparison</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IComparison</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIComparison(IComparison object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IEditable Comparison</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IEditable Comparison</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIEditableComparison(Editable object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMapping</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMapping</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMapping(IMapping object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IEditable Mapping</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IEditable Mapping</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIEditableMapping(
      org.eclipse.emf.diffmerge.api.IMapping.Editable object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMatch</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMatch</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMatch(IMatch object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IEditable Match</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IEditable Match</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIEditableMatch(
      org.eclipse.emf.diffmerge.api.IMatch.Editable object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IMergeable Difference</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IMergeable Difference</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIMergeableDifference(IMergeableDifference object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IEditable Mergeable Difference</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IEditable Mergeable Difference</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIEditableMergeableDifference(
      org.eclipse.emf.diffmerge.api.diff.IMergeableDifference.Editable object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IElement Relative Presence</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IElement Relative Presence</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIElementRelativePresence(IElementRelativePresence object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IElement Presence</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IElement Presence</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIElementPresence(IElementPresence object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IValue Presence</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IValue Presence</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIValuePresence(IValuePresence object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IAttribute Value Presence</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IAttribute Value Presence</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIAttributeValuePresence(IAttributeValuePresence object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>IReference Value Presence</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>IReference Value Presence</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIReferenceValuePresence(IReferenceValuePresence object) {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  public T defaultCase(EObject object) {
    return null;
  }

} //DiffdataSwitch
