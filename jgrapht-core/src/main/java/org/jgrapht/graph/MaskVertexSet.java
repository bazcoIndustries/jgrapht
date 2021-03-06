/*
 * (C) Copyright 2007-2016, by France Telecom and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.graph;

import java.util.*;

import org.jgrapht.util.*;
import org.jgrapht.util.PrefetchIterator.*;

/**
 * Helper for {@link MaskSubgraph}.
 *
 * @author Guillaume Boulmier
 * @since July 5, 2007
 */
class MaskVertexSet<V, E>
    extends AbstractSet<V>
{
    private MaskFunctor<V, E> mask;

    private Set<V> vertexSet;

    public MaskVertexSet(Set<V> vertexSet, MaskFunctor<V, E> mask)
    {
        this.vertexSet = vertexSet;
        this.mask = mask;
    }

    /**
     * @see java.util.Collection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o)
    {
        // Force a cast to type V. This is nonsense, of course, but
        // it's erased by the compiler anyway.
        V v = TypeUtil.uncheckedCast(o, null);

        // If o isn't a V, the first check will fail and
        // short-circuit, so we never try to test the mask on
        // non-vertex object inputs.
        return vertexSet.contains(v) && !mask.isVertexMasked(v);
    }

    /**
     * @see java.util.Set#iterator()
     */
    @Override
    public Iterator<V> iterator()
    {
        return new PrefetchIterator<V>(new MaskVertexSetNextElementFunctor());
    }

    /**
     * @see java.util.Set#size()
     */
    @Override
    public int size()
    {
        return (int) vertexSet.stream().filter(v -> contains(v)).count();
    }

    private class MaskVertexSetNextElementFunctor
        implements NextElementFunctor<V>
    {
        private Iterator<V> iter;

        public MaskVertexSetNextElementFunctor()
        {
            this.iter = MaskVertexSet.this.vertexSet.iterator();
        }

        @Override
        public V nextElement()
            throws NoSuchElementException
        {
            V element = this.iter.next();
            while (MaskVertexSet.this.mask.isVertexMasked(element)) {
                element = this.iter.next();
            }
            return element;
        }
    }
}

// End MaskVertexSet.java
