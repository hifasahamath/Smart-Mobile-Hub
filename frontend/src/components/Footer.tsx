import Link from 'next/link';
import { Smartphone, Mail, Phone, MapPin } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="bg-surface border-t border-border mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Brand */}
          <div className="md:col-span-1">
            <div className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-primary to-accent flex items-center justify-center">
                <Smartphone className="w-4 h-4 text-white" />
              </div>
              <span className="font-bold text-lg">Smart Mobile Hub</span>
            </div>
            <p className="text-sm text-muted leading-relaxed">
              Sri Lanka&apos;s premium mobile phone store. Genuine products, competitive prices, island-wide delivery.
            </p>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="font-semibold text-sm mb-4">Quick Links</h4>
            <nav className="flex flex-col gap-2">
              <Link href="/products" className="text-sm text-muted hover:text-foreground transition-colors">All Products</Link>
              <Link href="/products?featured=true" className="text-sm text-muted hover:text-foreground transition-colors">Featured</Link>
              <Link href="/products?trending=true" className="text-sm text-muted hover:text-foreground transition-colors">Trending</Link>
              <Link href="/ai-chat" className="text-sm text-muted hover:text-foreground transition-colors">AI Assistant</Link>
            </nav>
          </div>

          {/* Customer Service */}
          <div>
            <h4 className="font-semibold text-sm mb-4">Customer Service</h4>
            <nav className="flex flex-col gap-2">
              <Link href="/orders" className="text-sm text-muted hover:text-foreground transition-colors">Track Order</Link>
              <Link href="#" className="text-sm text-muted hover:text-foreground transition-colors">Return Policy</Link>
              <Link href="#" className="text-sm text-muted hover:text-foreground transition-colors">Warranty</Link>
              <Link href="#" className="text-sm text-muted hover:text-foreground transition-colors">FAQ</Link>
            </nav>
          </div>

          {/* Contact */}
          <div>
            <h4 className="font-semibold text-sm mb-4">Contact Us</h4>
            <div className="flex flex-col gap-3">
              <div className="flex items-center gap-2 text-sm text-muted">
                <MapPin className="w-4 h-4 shrink-0" />
                <span>Colombo, Sri Lanka</span>
              </div>
              <div className="flex items-center gap-2 text-sm text-muted">
                <Phone className="w-4 h-4 shrink-0" />
                <span>+94 11 234 5678</span>
              </div>
              <div className="flex items-center gap-2 text-sm text-muted">
                <Mail className="w-4 h-4 shrink-0" />
                <span>support@smartmobilehub.com</span>
              </div>
            </div>
          </div>
        </div>

        <div className="mt-10 pt-6 border-t border-border flex flex-col sm:flex-row items-center justify-between gap-4">
          <p className="text-xs text-muted">
            &copy; {new Date().getFullYear()} Smart Mobile Hub. All rights reserved.
          </p>
          <div className="flex items-center gap-4">
            <span className="text-xs text-muted">Cash on Delivery</span>
            <span className="text-xs text-muted">&bull;</span>
            <span className="text-xs text-muted">Bank Transfer</span>
            <span className="text-xs text-muted">&bull;</span>
            <span className="text-xs text-muted">Store Pickup</span>
          </div>
        </div>
      </div>
    </footer>
  );
}
