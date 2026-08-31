'use client';

import { useCart } from '@/lib/cart';
import { useAuth } from '@/lib/auth';
import { Minus, Plus, Trash2, ShoppingBag, ArrowRight } from 'lucide-react';
import Link from 'next/link';

export default function CartPage() {
  const { items, updateQuantity, removeItem, subtotal, itemCount } = useCart();
  const { isLoggedIn } = useAuth();

  if (items.length === 0) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-20 text-center">
        <ShoppingBag className="w-16 h-16 text-border mx-auto mb-4" />
        <h1 className="text-2xl font-bold mb-2">Your cart is empty</h1>
        <p className="text-muted mb-6">Start shopping to add items to your cart</p>
        <Link
          href="/products"
          className="inline-flex items-center gap-2 px-6 py-3 bg-primary text-white rounded-xl font-medium hover:bg-primary-hover transition-all"
        >
          Browse Products <ArrowRight className="w-4 h-4" />
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
      <h1 className="text-2xl font-bold mb-8">Shopping Cart ({itemCount} items)</h1>

      <div className="grid lg:grid-cols-3 gap-8">
        {/* Cart Items */}
        <div className="lg:col-span-2 space-y-4">
          {items.map((item) => (
            <div
              key={item.skuCode}
              className="bg-surface border border-border rounded-2xl p-4 flex gap-4 animate-fade-in"
            >
              {/* Image */}
              <div className="w-20 h-20 rounded-xl bg-surface-hover shrink-0 overflow-hidden">
                {item.imageUrl ? (
                  <img src={item.imageUrl} alt={item.productName} className="w-full h-full object-cover" />
                ) : (
                  <div className="w-full h-full flex items-center justify-center">
                    <ShoppingBag className="w-8 h-8 text-border" />
                  </div>
                )}
              </div>

              {/* Details */}
              <div className="flex-1 min-w-0">
                <Link href={`/products/${item.productSlug}`} className="font-semibold text-sm hover:text-primary transition-colors line-clamp-1">
                  {item.productName}
                </Link>
                <p className="text-xs text-muted mt-0.5">{item.variantDescription}</p>
                <p className="font-bold mt-2">Rs. {item.unitPrice.toLocaleString()}</p>
              </div>

              {/* Quantity + Remove */}
              <div className="flex flex-col items-end justify-between">
                <button
                  onClick={() => removeItem(item.skuCode)}
                  className="p-1.5 text-muted hover:text-danger transition-colors"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
                <div className="flex items-center border border-border rounded-lg">
                  <button
                    onClick={() => updateQuantity(item.skuCode, item.quantity - 1)}
                    className="p-1.5 hover:bg-surface-hover transition-colors"
                  >
                    <Minus className="w-3.5 h-3.5" />
                  </button>
                  <span className="px-3 text-sm font-medium">{item.quantity}</span>
                  <button
                    onClick={() => updateQuantity(item.skuCode, item.quantity + 1)}
                    className="p-1.5 hover:bg-surface-hover transition-colors"
                  >
                    <Plus className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Summary */}
        <div className="bg-surface border border-border rounded-2xl p-6 h-fit sticky top-24">
          <h2 className="font-semibold mb-4">Order Summary</h2>
          <div className="space-y-3 text-sm">
            <div className="flex justify-between">
              <span className="text-muted">Subtotal</span>
              <span className="font-medium">Rs. {subtotal.toLocaleString()}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-muted">Delivery</span>
              <span className="text-sm text-muted">Calculated at checkout</span>
            </div>
            <div className="border-t border-border pt-3 flex justify-between">
              <span className="font-semibold">Total</span>
              <span className="font-bold text-lg">Rs. {subtotal.toLocaleString()}</span>
            </div>
          </div>

          {isLoggedIn ? (
            <Link
              href="/checkout"
              className="block w-full mt-6 py-3 bg-primary text-white text-center rounded-xl font-semibold hover:bg-primary-hover transition-all"
            >
              Proceed to Checkout
            </Link>
          ) : (
            <Link
              href="/login?redirect=/checkout"
              className="block w-full mt-6 py-3 bg-primary text-white text-center rounded-xl font-semibold hover:bg-primary-hover transition-all"
            >
              Sign In to Checkout
            </Link>
          )}

          <Link
            href="/products"
            className="block text-center text-sm text-primary mt-3 hover:text-primary-hover transition-colors"
          >
            Continue Shopping
          </Link>
        </div>
      </div>
    </div>
  );
}
