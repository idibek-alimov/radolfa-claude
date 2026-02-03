'use client';

import Link from 'next/link';

export default function Navbar() {
  return (
    <nav className="bg-white shadow-sm sticky top-0 z-10">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Brand */}
          <Link href="/" className="text-xl font-bold text-indigo-600">
            Radolfa
          </Link>

          {/* Links */}
          <div className="flex items-center gap-6">
            <Link
              href="/"
              className="text-gray-700 hover:text-indigo-600 text-sm font-medium transition-colors"
            >
              Home
            </Link>
            <Link
              href="/products"
              className="text-gray-700 hover:text-indigo-600 text-sm font-medium transition-colors"
            >
              Products
            </Link>
            <Link
              href="/login"
              className="text-indigo-600 hover:text-indigo-700 text-sm font-semibold transition-colors"
            >
              Login
            </Link>
          </div>
        </div>
      </div>
    </nav>
  );
}
