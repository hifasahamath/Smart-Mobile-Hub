'use client';

import { useEffect, useState } from 'react';
import { useCart } from '@/lib/cart';
import { useAuth } from '@/lib/auth';
import { api } from '@/lib/api';
import { useQuery } from '@tanstack/react-query';
import type { DeliveryZone, Order } from '@/lib/types';
import { Loader2, CheckCircle, ArrowLeft } from 'lucide-react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

export default function CheckoutPage() {
  const { items, subtotal, clearCart } = useCart();
  const { isLoggedIn, user } = useAuth();
  const router = useRouter();

  const [deliveryMethod, setDeliveryMethod] = useState<'HOME_DELIVERY' | 'STORE_PICKUP'>('HOME_DELIVERY');
  const [paymentMethod, setPaymentMethod] = useState<'CASH_ON_DELIVERY' | 'BANK_TRANSFER' | 'PAY_AT_STORE'>('CASH_ON_DELIVERY');
  const [deliveryAddress, setDeliveryAddress] = useState('');
  const [deliveryCity, setDeliveryCity] = useState('');
  const [deliveryZoneName, setDeliveryZoneName] = useState('');
  const [contactName, setContactName] = useState(user?.fullName || '');
  const [contactPhone, setContactPhone] = useState('');
  const [notes, setNotes] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [orderPlaced, setOrderPlaced] = useState<Order | null>(null);

  const { data: zones } = useQuery({
    queryKey: ['delivery-zones'],
    queryFn: async () => {
      const res = await api.get<DeliveryZone[]>('/api/v1/delivery-zones');
      return res.data;
    },
  });

  const selectedZone = zones?.find(z => z.name === deliveryZoneName);
  const deliveryFee = deliveryMethod === 'HOME_DELIVERY' ? (selectedZone?.deliveryFee || 0) : 0;
  const total = subtotal + deliveryFee;

  useEffect(() => {
    if (!isLoggedIn) {
      router.push('/login?redirect=/checkout');
    }
  }, [isLoggedIn, router]);

  if (!isLoggedIn) {
    return null;
  }

  if (items.length === 0 && !orderPlaced) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-20 text-center">
        <h1 className="text-2xl font-bold mb-2">Cart is empty</h1>
        <Link href="/products" className="text-primary">Browse Products</Link>
      </div>
    );
  }

  if (orderPlaced) {
    return (
      <div className="max-w-lg mx-auto px-4 sm:px-6 py-20 text-center animate-slide-up">
        <CheckCircle className="w-16 h-16 text-success mx-auto mb-4" />
        <h1 className="text-2xl font-bold mb-2">Order Placed!</h1>
        <p className="text-muted mb-2">Your order <strong>{orderPlaced.orderNumber}</strong> has been created.</p>
        <p className="text-sm text-muted mb-6">We&apos;ll process it shortly.</p>
        <Link href="/orders" className="inline-flex px-6 py-3 bg-primary text-white rounded-xl font-medium hover:bg-primary-hover transition-all">
          View My Orders
        </Link>
      </div>
    );
  }

  const handleCheckout = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const res = await api.post<Order>('/api/v1/orders', {
        items: items.map(i => ({
          skuCode: i.skuCode,
          productName: i.productName,
          variantDescription: i.variantDescription,
          quantity: i.quantity,
        })),
        deliveryMethod,
        deliveryAddress,
        deliveryCity,
        deliveryZoneName,
        contactName,
        contactPhone,
        paymentMethod,
        notes,
      });

      setOrderPlaced(res.data);
      clearCart();
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 py-8">
      <Link href="/cart" className="inline-flex items-center gap-1 text-sm text-muted hover:text-foreground mb-6 transition-colors">
        <ArrowLeft className="w-4 h-4" /> Back to Cart
      </Link>
      <h1 className="text-2xl font-bold mb-8">Checkout</h1>

      <form onSubmit={handleCheckout}>
        <div className="grid lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-6">
            {error && (
              <div className="p-3 bg-danger/10 border border-danger/20 rounded-lg text-sm text-danger">{error}</div>
            )}

            {/* Contact */}
            <div className="bg-surface border border-border rounded-2xl p-5">
              <h2 className="font-semibold mb-4">Contact Information</h2>
              <div className="grid sm:grid-cols-2 gap-4">
                <div>
                  <label className="text-sm font-medium mb-1.5 block">Full Name</label>
                  <input type="text" value={contactName} onChange={e => setContactName(e.target.value)} required
                    className="w-full px-4 py-2.5 bg-background border border-border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50" />
                </div>
                <div>
                  <label className="text-sm font-medium mb-1.5 block">Phone Number</label>
                  <input type="tel" value={contactPhone} onChange={e => setContactPhone(e.target.value)} required placeholder="+94 77 123 4567"
                    className="w-full px-4 py-2.5 bg-background border border-border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50" />
                </div>
              </div>
            </div>

            {/* Delivery */}
            <div className="bg-surface border border-border rounded-2xl p-5">
              <h2 className="font-semibold mb-4">Delivery Method</h2>
              <div className="grid sm:grid-cols-2 gap-3 mb-4">
                {(['HOME_DELIVERY', 'STORE_PICKUP'] as const).map((method) => (
                  <button key={method} type="button" onClick={() => setDeliveryMethod(method)}
                    className={`p-4 rounded-xl border text-left transition-all ${deliveryMethod === method ? 'border-primary bg-primary/5' : 'border-border hover:border-primary/30'}`}>
                    <span className="font-medium text-sm">{method === 'HOME_DELIVERY' ? '🚚 Home Delivery' : '🏪 Store Pickup'}</span>
                    <p className="text-xs text-muted mt-1">{method === 'HOME_DELIVERY' ? 'Delivered to your door' : 'Pick up from our store'}</p>
                  </button>
                ))}
              </div>

              {deliveryMethod === 'HOME_DELIVERY' && (
                <div className="space-y-4">
                  <div>
                    <label className="text-sm font-medium mb-1.5 block">Delivery Zone</label>
                    <select value={deliveryZoneName} onChange={e => setDeliveryZoneName(e.target.value)} required
                      className="w-full px-4 py-2.5 bg-background border border-border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50">
                      <option value="">Select zone</option>
                      {zones?.map(z => (
                        <option key={z.id} value={z.name}>{z.name} — Rs. {z.deliveryFee} ({z.estimatedDays} day{z.estimatedDays > 1 ? 's' : ''})</option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label className="text-sm font-medium mb-1.5 block">Address</label>
                    <textarea value={deliveryAddress} onChange={e => setDeliveryAddress(e.target.value)} required rows={2}
                      className="w-full px-4 py-2.5 bg-background border border-border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50" />
                  </div>
                  <div>
                    <label className="text-sm font-medium mb-1.5 block">City</label>
                    <input type="text" value={deliveryCity} onChange={e => setDeliveryCity(e.target.value)}
                      className="w-full px-4 py-2.5 bg-background border border-border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50" />
                  </div>
                </div>
              )}
            </div>

            {/* Payment */}
            <div className="bg-surface border border-border rounded-2xl p-5">
              <h2 className="font-semibold mb-4">Payment Method</h2>
              <div className="space-y-3">
                {([
                  { value: 'CASH_ON_DELIVERY', label: '💵 Cash on Delivery', desc: 'Pay when you receive' },
                  { value: 'BANK_TRANSFER', label: '🏦 Bank Transfer', desc: 'Transfer to our account' },
                  { value: 'PAY_AT_STORE', label: '🏪 Pay at Store', desc: 'Pay when you pick up' },
                ] as const).map((pm) => (
                  <button key={pm.value} type="button" onClick={() => setPaymentMethod(pm.value)}
                    className={`w-full p-4 rounded-xl border text-left transition-all ${paymentMethod === pm.value ? 'border-primary bg-primary/5' : 'border-border hover:border-primary/30'}`}>
                    <span className="font-medium text-sm">{pm.label}</span>
                    <p className="text-xs text-muted mt-0.5">{pm.desc}</p>
                  </button>
                ))}
              </div>
            </div>

            {/* Notes */}
            <div className="bg-surface border border-border rounded-2xl p-5">
              <h2 className="font-semibold mb-4">Order Notes (optional)</h2>
              <textarea value={notes} onChange={e => setNotes(e.target.value)} rows={2} placeholder="Any special instructions..."
                className="w-full px-4 py-2.5 bg-background border border-border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50" />
            </div>
          </div>

          {/* Summary sidebar */}
          <div className="bg-surface border border-border rounded-2xl p-6 h-fit sticky top-24">
            <h2 className="font-semibold mb-4">Order Summary</h2>
            <div className="space-y-3 mb-4">
              {items.map(item => (
                <div key={item.skuCode} className="flex justify-between text-sm">
                  <span className="text-muted truncate mr-2">{item.productName} × {item.quantity}</span>
                  <span className="font-medium shrink-0">Rs. {(item.unitPrice * item.quantity).toLocaleString()}</span>
                </div>
              ))}
            </div>
            <div className="border-t border-border pt-3 space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-muted">Subtotal</span>
                <span>Rs. {subtotal.toLocaleString()}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted">Delivery</span>
                <span>{deliveryFee > 0 ? `Rs. ${deliveryFee.toLocaleString()}` : 'Free'}</span>
              </div>
              <div className="border-t border-border pt-2 flex justify-between font-bold">
                <span>Total</span>
                <span className="text-lg">Rs. {total.toLocaleString()}</span>
              </div>
            </div>
            <button type="submit" disabled={loading}
              className="w-full mt-6 py-3 bg-primary text-white rounded-xl font-semibold hover:bg-primary-hover disabled:opacity-50 transition-all flex items-center justify-center gap-2">
              {loading && <Loader2 className="w-4 h-4 animate-spin" />}
              Place Order
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
