import { Link, useNavigate } from 'react-router';
import { ImageWithFallback } from '../components/figma/ImageWithFallback';
import { Search } from 'lucide-react';
import { useState, type FormEvent } from 'react';

import { BRANDS } from '../constants/brands';

export default function HomePage() {
  return (
    <main>
      {/* Hero Section */}
      <section className="relative h-[500px] flex items-center justify-center overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900">
          <ImageWithFallback
            src="https://images.unsplash.com/photo-1765914448097-85ef5e550056?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxhdGhsZXRpYyUyMHNwb3J0c3dlYXIlMjBydW5uaW5nfGVufDF8fHx8MTc2ODQ0ODg0OHww&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral"
            alt="Athletic sportswear background"
            className="w-full h-full object-cover opacity-30"
          />
        </div>
        <div className="relative z-10 text-center px-6">
          <h1 className="text-5xl md:text-6xl font-bold text-white mb-4 tracking-tight">
            Discover Your Style with Wearly
          </h1>
          <p className="text-xl text-gray-200 max-w-2xl mx-auto">
            Curated fashion from the world's leading brands
          </p>
        </div>
      </section>

      {/* Brand Cards Section */}
      <section className="max-w-[1400px] mx-auto px-8 py-20">
        <div className="text-center mb-12">
          <h2 className="text-3xl font-bold text-gray-900 mb-3">
            Featured Brands
          </h2>
          <p className="text-gray-600">
            Explore our curated collection of premium fashion brands
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {BRANDS.map((brand) => (
            <Link
              key={brand.id}
              to={`/brand/${brand.id}`}
              className="group bg-white rounded-lg shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden border border-gray-100"
            >
              <div className="p-8 h-[200px] flex flex-col justify-center items-center text-center bg-gradient-to-br from-gray-50 to-white group-hover:from-gray-100 group-hover:to-gray-50 transition-all duration-300">
                <h3 className="text-3xl font-bold text-gray-900 mb-3 tracking-tight group-hover:scale-105 transition-transform duration-300">
                  {brand.name}
                </h3>
                <p className="text-sm text-gray-600 uppercase tracking-wider">
                  {brand.tagline}
                </p>
              </div>
              <div className="px-8 py-4 bg-gray-50 border-t border-gray-100">
                <p className="text-sm text-gray-500 text-center group-hover:text-gray-700 transition-colors">
                  Shop Now →
                </p>
              </div>
            </Link>
          ))}
        </div>
      </section>

      {/* Search Section */}
      <section className="bg-white border-t border-gray-100 py-20">
        <div className="max-w-[1400px] mx-auto px-8 flex flex-col items-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">
            Looking for something specific?
          </h2>
          <form
            onSubmit={(e: FormEvent) => {
              e.preventDefault();
              const form = e.target as HTMLFormElement;
              const input = form.elements.namedItem('q') as HTMLInputElement;
              if (input.value.trim()) {
                window.location.href = `/search?keyword=${encodeURIComponent(input.value)}`;
              }
            }}
            className="relative w-full max-w-xl"
          >
            <input
              name="q"
              type="text"
              placeholder="Search products..."
              className="w-full h-12 pl-4 pr-24 text-gray-900 bg-gray-50 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
            />
            <button
              type="submit"
              className="absolute right-1 top-1/2 -translate-y-1/2 h-10 px-6 bg-gray-900 text-white text-sm rounded-md hover:bg-gray-800 transition-colors"
            >
              <Search className="w-4 h-4" />
            </button>
          </form>
        </div>
      </section>
    </main >
  );
}