'use client';

import { useTopProducts } from '@/hooks/useProducts';
import ProductCard from '@/components/ProductCard';

/**
 * Homepage – displays the top-selling product collection.
 * Fetched via TanStack Query; shows a loading state while the
 * backend responds, then renders a responsive card grid.
 */
export default function HomePage() {
  const { data: products, isLoading } = useTopProducts();

  return (
    <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-2">
        Welcome to Radolfa
      </h1>
      <p className="text-gray-500 mb-6">Discover our top-selling pieces</p>

      {isLoading && <p className="text-gray-500 py-8">Loading…</p>}

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 sm:gap-6">
        {products?.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>

      {!isLoading && products?.length === 0 && (
        <p className="text-gray-500 py-8">No featured products yet.</p>
      )}
    </section>
  );
}
