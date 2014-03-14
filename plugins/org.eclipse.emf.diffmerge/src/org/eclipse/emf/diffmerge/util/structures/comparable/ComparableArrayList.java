/**
 * <copyright>
 * 
 * Copyright (c) 2013 Thales Global Services S.A.S.
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
package org.eclipse.emf.diffmerge.util.structures.comparable;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.diffmerge.util.structures.StructuresUtil;
import org.eclipse.emf.diffmerge.util.structures.comparable.IComparableStructure.IComparableList;


/**
 * A BasicEList which is Comparable based on its members.
 * Null values are accepted.
 * @param <E> the type of the elements in the list
 * @see BasicEList
 * @author Olivier Constant
 */
public class ComparableArrayList<E extends Comparable<?>> extends BasicEList<E>
implements IComparableList<E> {
  
  private static final long serialVersionUID = 1L;
  
  /** Whether equals rather than == must be used to compare members */
  private final boolean _useEquals;
  
  
  /**
   * Constructor for empty list with default capacity and usage of == to compare members
   * @see BasicEList#BasicEList()
   */
  public ComparableArrayList() {
    this(true);
  }
  
  /**
   * Constructor for empty list with default capacity
   * @param useEquals_p whether equals rather than == must be used to compare members
   * @see BasicEList#BasicEList()
   */
  public ComparableArrayList(boolean useEquals_p) {
    super();
    _useEquals = useEquals_p;
  }
  
  /**
   * Constructor for empty list with the given initial capacity
   * @see BasicEList#BasicEList(int)
   * @param initialCapacity_p the initial capacity of the list before it must grow
   * @param useEquals_p whether equals rather than == must be used to compare members
   */
  public ComparableArrayList(int initialCapacity_p, boolean useEquals_p) {
    super(initialCapacity_p);
    _useEquals = useEquals_p;
  }
  
  /**
   * Constructor for a list filled with the elements of the given collection
   * @param collection_p a non-null, potentially empty collection
   * @param useEquals_p whether equals rather than == must be used to compare members
   */
  public ComparableArrayList(Collection<? extends E> collection_p,
      boolean useEquals_p)  {
    super(collection_p);
    _useEquals = useEquals_p;
  }
  
  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(IComparableStructure<?> o_p) {
    return STRUCTURE_COMPARATOR.compare(this, o_p);
  }
  
  /**
   * @see org.eclipse.emf.diffmerge.util.structures.comparable.IComparableStructure#getCompareIterator()
   */
  public Iterator<E> getCompareIterator() {
    return iterator();
  }
  
  /**
   * @see org.eclipse.emf.common.util.AbstractEList#toString()
   */
  @Override
  public String toString() {
    return StructuresUtil.toCollectionString(this);
  }
  
  /**
   * @see org.eclipse.emf.common.util.AbstractEList#useEquals()
   */
  @Override
  protected boolean useEquals() {
    return _useEquals;
  }
  
}
