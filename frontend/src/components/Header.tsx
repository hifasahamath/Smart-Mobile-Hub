'use client';

import Link from 'next/link';
import { useState } from 'react';
import { ShoppingCart, Menu, X, Search, User, Smartphone, MessageCircle } from 'lucide-react';
import { useCart } from '@/lib/cart';
import { useAuth } from '@/lib/auth';

export default function Header() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [searchOpen, setSearchOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const { itemCount } = useCart();
  const { isLoggedIn, isAdmin, user, logout } = useAuth();

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      window.location.href = `/products?search=${encodeURIComponent(searchQuery.trim())}`;
      setSearchOpen(false);
    }
  };

  return (
    <header className="sticky top-0 z-50 border-b border-border bg-surface/80 backdrop-blur-xl">
      {/* Top bar */}
      <div className="bg-primary text-white text-center text-xs py-1.5 font-medium tracking-wide">
        🔥 Free delivery on orders over Rs. 5,000 &bull; Island-wide delivery available
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link href="/" className="flex items-center gap-2 group">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-primary to-accent flex items-center justify-center group-hover:scale-105 transition-transform">
              <Smartphone className="w-5 h-5 text-white" />
            </div>
            <div>
              <span className="font-bold text-lg tracking-tight">Smart Mobile</span>
              <span className="text-primary font-bold text-lg"> Hub</span>
            </div>
          </Link>

          {/* Desktop Nav */}
          <nav className="hidden md:flex items-center gap-8">
            <Link href="/products" className="text-sm font-medium text-muted hover:text-foreground transition-colors">
              All Phones
            </Link>
            <Link href="/products?category=smartphones" className="text-sm font-medium text-muted hover:text-foreground transition-colors">
              Smartphones
            </Link>
            <Link href="/products?category=accessories" className="text-sm font-medium text-muted hover:text-foreground transition-colors">
              Accessories
            </Link>
            <Link href="/ai-chat" className="text-sm font-medium text-primary hover:text-primary-hover transition-colors flex items-center gap-1">
              <MessageCircle className="w-4 h-4" />
              Ask AI
            </Link>
          </nav>

          {/* Actions */}
          <div className="flex items-center gap-3">
            {/* Search button */}
            <button
              onClick={() => setSearchOpen(!searchOpen)}
              className="p-2 rounded-lg text-muted hover:text-foreground hover:bg-surface-hover transition-all"
              aria-label="Search"
            >
              <Search className="w-5 h-5" />
            </button>

            {/* Cart */}
            <Link
              href="/cart"
              className="p-2 rounded-lg text-muted hover:text-foreground hover:bg-surface-hover transition-all relative"
            >
              <ShoppingCart className="w-5 h-5" />
              {itemCount > 0 && (
                <span className="absolute -top-1 -right-1 bg-primary text-white text-[10px] font-bold w-5 h-5 rounded-full flex items-center justify-center animate-pulse-glow">
                  {itemCount}
                </span>
              )}
            </Link>

            {/* User */}
            {isLoggedIn ? (
              <div className="relative group">
                <button className="p-2 rounded-lg text-muted hover:text-foreground hover:bg-surface-hover transition-all">
                  <User className="w-5 h-5" />
                </button>
                <div className="absolute right-0 top-full mt-2 w-56 bg-surface border border-border rounded-xl shadow-xl opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all duration-200 p-2">
                  <div className="px-3 py-2 text-sm font-medium border-b border-border mb-1">
                    {user?.fullName}
                  </div>
                  <Link href="/orders" className="block px-3 py-2 text-sm text-muted hover:text-foreground hover:bg-surface-hover rounded-lg transition-colors">
                    My Orders
                  </Link>
                  {isAdmin && (
                    <Link href="/admin" className="block px-3 py-2 text-sm text-primary hover:bg-surface-hover rounded-lg transition-colors">
                      Admin Dashboard
                    </Link>
                  )}
                  <button
                    onClick={logout}
                    className="w-full text-left px-3 py-2 text-sm text-danger hover:bg-surface-hover rounded-lg transition-colors"
                  >
                    Sign Out
                  </button>
                </div>
              </div>
            ) : (
              <Link
                href="/login"
                className="hidden sm:inline-flex px-4 py-2 text-sm font-medium bg-primary text-white rounded-lg hover:bg-primary-hover transition-colors"
              >
                Sign In
              </Link>
            )}

            {/* Mobile menu toggle */}
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="md:hidden p-2 rounded-lg text-muted hover:text-foreground hover:bg-surface-hover transition-all"
              aria-label="Menu"
            >
              {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
            </button>
          </div>
        </div>

        {/* Search bar */}
        {searchOpen && (
          <div className="py-3 border-t border-border animate-fade-in">
            <form onSubmit={handleSearch} className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search phones, brands, accessories..."
                className="w-full pl-10 pr-4 py-2.5 bg-background border border-border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary transition-all"
                autoFocus
              />
            </form>
          </div>
        )}

        {/* Mobile menu */}
        {mobileMenuOpen && (
          <div className="md:hidden py-4 border-t border-border animate-fade-in">
            <nav className="flex flex-col gap-1">
              <Link href="/products" className="px-3 py-2.5 text-sm font-medium text-muted hover:text-foreground hover:bg-surface-hover rounded-lg transition-colors" onClick={() => setMobileMenuOpen(false)}>
                All Phones
              </Link>
              <Link href="/products?category=smartphones" className="px-3 py-2.5 text-sm font-medium text-muted hover:text-foreground hover:bg-surface-hover rounded-lg transition-colors" onClick={() => setMobileMenuOpen(false)}>
                Smartphones
              </Link>
              <Link href="/products?category=accessories" className="px-3 py-2.5 text-sm font-medium text-muted hover:text-foreground hover:bg-surface-hover rounded-lg transition-colors" onClick={() => setMobileMenuOpen(false)}>
                Accessories
              </Link>
              <Link href="/ai-chat" className="px-3 py-2.5 text-sm font-medium text-primary hover:bg-surface-hover rounded-lg transition-colors flex items-center gap-2" onClick={() => setMobileMenuOpen(false)}>
                <MessageCircle className="w-4 h-4" />
                Ask AI Assistant
              </Link>
              {!isLoggedIn && (
                <Link href="/login" className="mt-2 px-3 py-2.5 text-sm font-medium bg-primary text-white rounded-lg text-center hover:bg-primary-hover transition-colors" onClick={() => setMobileMenuOpen(false)}>
                  Sign In
                </Link>
              )}
            </nav>
          </div>
        )}
      </div>
    </header>
  );
}
