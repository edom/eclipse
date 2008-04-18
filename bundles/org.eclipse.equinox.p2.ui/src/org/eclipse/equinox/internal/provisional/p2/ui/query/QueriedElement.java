/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.provisional.p2.ui.query;

import org.eclipse.equinox.internal.p2.ui.model.ProvElement;
import org.eclipse.equinox.internal.provisional.p2.query.IQueryable;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.IQueryProvider;

/**
 * Element class that represents an element that gets its children
 * by using a query.
 * 
 * @since 3.4
 *
 */
public abstract class QueriedElement extends ProvElement {

	IQueryProvider queryProvider;
	protected IQueryable queryable;
	protected QueryContext queryContext = null;

	protected QueriedElement(QueryContext queryContext) {
		this.queryContext = queryContext;
	}

	public Object[] getChildren(Object o) {
		if (queryProvider == null)
			return new Object[0];
		ElementQueryDescriptor queryDescriptor = getQueryProvider().getQueryDescriptor(this, getQueryType());
		if (queryDescriptor == null)
			return new Object[0];
		queryDescriptor.queryable.query(queryDescriptor.query, queryDescriptor.collector, null);
		return queryDescriptor.collector.toArray(Object.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
	 */
	public String getLabel(Object o) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.equinox.internal.provisional.p2.ui.model.ProvElement#getParent(java.lang.Object)
	 */
	public Object getParent(Object o) {
		return null;
	}

	/**
	 * Return the query type that is appropriate for this element when there
	 * is no query context.
	 * @return  The integer query type
	 */
	protected abstract int getDefaultQueryType();

	/**
	 * Return the query type that should be used for this element.
	 * Depending on the element, the query type may take the query context
	 * into account.  Subclasses should override this method if there are
	 * context-dependent decisions to be made to determine the query.
	 * @return The integer query type
	 */
	protected int getQueryType() {
		return getDefaultQueryType();
	}

	public void setQueryProvider(IQueryProvider queryProvider) {
		this.queryProvider = queryProvider;
	}

	public IQueryProvider getQueryProvider() {
		return queryProvider;
	}

	public void setQueryable(IQueryable queryable) {
		this.queryable = queryable;
	}

	public QueryContext getQueryContext() {
		return queryContext;
	}

	public void setQueryContext(QueryContext context) {
		queryContext = context;
	}

	public IQueryable getQueryable() {
		return queryable;
	}

	/**
	 * Return a boolean indicating whether the receiver
	 * has enough information to get its queryable.  This is used in lieu
	 * of {{@link #getQueryable()} when lazy initialization
	 * of the queryable is not desired, and a client wishes
	 * to know whether the queryable could be obtained.  Subclasses
	 * that cache information needed to retrieve the queryable rather
	 * than the queryable itself should
	 * override this. 
	 * 
	 * @return <code>true</code> if the receiver has enough
	 * information to retrieve its queryable, <code>false</code> 
	 * if it does not.
	 */
	public boolean knowsQueryable() {
		return queryable != null;
	}

	/**
	 * Return a boolean indicating whether the receiver
	 * actually has its queryable.  This is used in lieu
	 * of {{@link #getQueryable()} when lazy initialization
	 * of the queryable is not desired.  For example, when
	 * working with an element whose queryable may be 
	 * expensive to obtain, clients may check this before
	 * actually getting the queryable.  Subclasses
	 * should typically not need to override this.
	 * 
	 * @return <code>true</code> if the receiver has its
	 * queryable, <code>false</code> if it does not yet.
	 */
	public boolean hasQueryable() {
		return queryable != null;
	}

}
