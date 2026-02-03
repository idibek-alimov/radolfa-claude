'use client';

import Image from 'next/image';
import { useParams } from 'next/navigation';
import { useProduct } from '@/hooks/useProducts';

export default function ProductDetailPage() {
  const { id } = useParams();
  const { data: product, isLoading, error } = useProduct(id as string);

  // ---- loading ----
  if (isLoading) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-16 text-center">
        <p className="text-gray-500">Loading…</p>
      </div>
    );
  }

  // ---- error / not found ----
  if (error || !product) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-16 text-center">
        <p className="text-gray-500">Product not found.</p>
      </div>
    );
  }

  // ---- product detail ----
  return (
    <section className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Hero image */}
        <div className="relative aspect-square rounded-lg overflow-hidden bg-gray-100">
          {product.images.length > 0 ? (
            <Image
              src={product.images[0]}
              alt={product.name}
              fill
              className="object-cover"
              sizes="(max-width: 768px) 100vw, 50vw"
            />
          ) : (
            <div className="flex items-center justify-center h-full">
              <span className="text-gray-400">No image available</span>
            </div>
          )}
        </div>

        {/* Details */}
        <div className="flex flex-col justify-center">
          <h1 className="text-3xl font-bold text-gray-900">{product.name}</h1>
          <p className="text-2xl font-semibold text-indigo-600 mt-2">
            ${product.price.toFixed(2)}
          </p>

          {product.webDescription && (
            <p className="text-gray-600 mt-4 leading-relaxed">
              {product.webDescription}
            </p>
          )}

          <div className="mt-6">
            {product.stock > 0 ? (
              <span className="text-green-600 font-medium">
                In stock ({product.stock} available)
              </span>
            ) : (
              <span className="text-red-600 font-medium">Out of stock</span>
            )}
          </div>
        </div>
      </div>
    </section>
  );
}
