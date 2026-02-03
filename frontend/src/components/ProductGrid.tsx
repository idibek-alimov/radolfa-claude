'use client';

import { useEffect, useRef } from 'react';
import { useCatalogProducts } from '@/hooks/useProducts';
import ProductCard from './ProductCard';

/**
 * Infinite-scroll product grid.
 * An IntersectionObserver on the sentinel {@code <div>} at the bottom
 * triggers the next-page fetch when the user scrolls near the end.
 */
export default function ProductGrid() {
  const { data, fetchNextPage, hasNextPage, isLoading, isFetchingNextPage } =
    useCatalogProducts();
  const sentinel = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const el = sentinel.current;
    if (!el) return;

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasNextPage) {
          fetchNextPage();
        }
      },
      { threshold: 0.1 },
    );

    observer.observe(el);
    return () => observer.disconnect();
  }, [fetchNextPage, hasNextPage]);

  // ---- loading / empty states ----
  if (isLoading) {
    return <p className="text-center text-gray-500 py-12">Loading products…</p>;
  }

  const products = data?.pages.flatMap((page) => page) ?? [];

  if (products.length === 0) {
    return (
      <p className="text-center text-gray-500 py-12">No products found.</p>
    );
  }

  return (
    <div>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 sm:gap-6">
        {products.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>

      {/* Sentinel for infinite scroll */}
      <div ref={sentinel} className="h-10" />

      {isFetchingNextPage && (
        <p className="text-center text-gray-500 py-4">Loading more…</p>
      )}
    </div>
  );
}
