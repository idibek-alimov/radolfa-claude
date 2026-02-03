import ProductGrid from '@/components/ProductGrid';

export const metadata = {
  title: 'Product Catalog — Radolfa',
  description: 'Browse our full product catalog.',
};

/**
 * Server component shell – renders the client-side infinite-scroll grid.
 */
export default function CatalogPage() {
  return (
    <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">All Products</h1>
      <ProductGrid />
    </section>
  );
}
