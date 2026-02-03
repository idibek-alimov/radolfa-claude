'use client';

import Image from 'next/image';
import Link from 'next/link';
import { Product } from '@/hooks/useProducts';

/** Grey SVG placeholder used when no S3 image is available. */
const PLACEHOLDER =
  'data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%22300%22%20height%3D%22300%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Crect%20width%3D%22300%22%20height%3D%22300%22%20fill%3D%22%23e5e7eb%22%2F%3E%3Ctext%20x%3D%22150%22%20y%3D%22150%22%20font-size%3D%2214%22%20text-anchor%3D%22middle%22%20fill%3D%22%236b7280%22%20dominant-baseline%3D%22middle%22%3ENo%20Image%3C%2Ftext%3E%3C%2Fsvg%3E';

export default function ProductCard({ product }: { product: Product }) {
  const imageSrc = product.images.length > 0 ? product.images[0] : PLACEHOLDER;

  return (
    <Link
      href={`/products/${product.id}`}
      className="block rounded-lg border border-gray-200 overflow-hidden hover:shadow-md transition-shadow bg-white"
    >
      {/* Thumbnail */}
      <div className="relative h-56">
        <Image
          src={imageSrc}
          alt={product.name}
          fill
          className="object-cover"
          sizes="(max-width: 640px) 100vw, (max-width: 1024px) 50vw, 25vw"
        />
      </div>

      {/* Info strip */}
      <div className="p-4">
        <h3 className="font-semibold text-gray-900 truncate">{product.name}</h3>
        <p className="text-lg font-bold text-indigo-600 mt-1">
          ${product.price.toFixed(2)}
        </p>
        {product.stock === 0 && (
          <span className="inline-block mt-1 text-xs text-red-600 font-medium">
            Out of stock
          </span>
        )}
      </div>
    </Link>
  );
}
