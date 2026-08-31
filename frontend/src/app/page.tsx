import Link from 'next/link';
import { ArrowRight, Shield, Truck, CreditCard, Star, Smartphone, Headphones, Watch, Battery } from 'lucide-react';

const categories = [
  { name: 'Smartphones', slug: 'smartphones', icon: Smartphone, color: 'from-violet-500 to-purple-600' },
  { name: 'Accessories', slug: 'accessories', icon: Headphones, color: 'from-amber-500 to-orange-600' },
  { name: 'Smartwatches', slug: 'smartwatches', icon: Watch, color: 'from-emerald-500 to-teal-600' },
  { name: 'Power Banks', slug: 'power-banks', icon: Battery, color: 'from-blue-500 to-indigo-600' },
];

const brands = [
  { name: 'Apple', slug: 'apple' },
  { name: 'Samsung', slug: 'samsung' },
  { name: 'Google', slug: 'google' },
  { name: 'Xiaomi', slug: 'xiaomi' },
  { name: 'OnePlus', slug: 'oneplus' },
  { name: 'Nothing', slug: 'nothing' },
];

const features = [
  { icon: Truck, title: 'Island-wide Delivery', description: 'Fast delivery across Sri Lanka' },
  { icon: Shield, title: '100% Genuine', description: 'Authentic products with warranty' },
  { icon: CreditCard, title: 'Flexible Payment', description: 'COD, bank transfer, store pickup' },
  { icon: Star, title: 'Best Prices', description: 'Competitive pricing guaranteed' },
];

export default function HomePage() {
  return (
    <>
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-br from-primary/5 via-background to-accent/5">
        <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_top_right,_var(--tw-gradient-stops))] from-primary/10 via-transparent to-transparent" />
        <div className="max-w-7xl mx-auto px-4 sm:px-6 py-16 md:py-24 relative">
          <div className="max-w-2xl">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary/10 text-primary text-xs font-semibold mb-6 animate-fade-in">
              <span className="w-1.5 h-1.5 rounded-full bg-primary animate-pulse" />
              New Arrivals Available
            </div>
            <h1 className="text-4xl md:text-6xl font-bold tracking-tight leading-[1.1] mb-6 animate-slide-up">
              Your Next Phone,{' '}
              <span className="bg-gradient-to-r from-primary to-accent bg-clip-text text-transparent">
                Delivered Fast
              </span>
            </h1>
            <p className="text-lg text-muted mb-8 max-w-lg animate-slide-up" style={{ animationDelay: '0.1s' }}>
              Sri Lanka&apos;s premium mobile hub. Latest smartphones from Apple, Samsung, Google & more — with AI-powered recommendations.
            </p>
            <div className="flex flex-wrap gap-3 animate-slide-up" style={{ animationDelay: '0.2s' }}>
              <Link
                href="/products"
                className="inline-flex items-center gap-2 px-6 py-3 bg-primary text-white rounded-xl font-medium hover:bg-primary-hover transition-all hover:gap-3"
              >
                Shop Now <ArrowRight className="w-4 h-4" />
              </Link>
              <Link
                href="/ai-chat"
                className="inline-flex items-center gap-2 px-6 py-3 bg-surface border border-border text-foreground rounded-xl font-medium hover:bg-surface-hover transition-all"
              >
                Ask AI Assistant
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* Categories */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 py-16">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h2 className="text-2xl font-bold">Shop by Category</h2>
            <p className="text-sm text-muted mt-1">Find exactly what you need</p>
          </div>
          <Link href="/products" className="text-sm text-primary font-medium hover:text-primary-hover transition-colors flex items-center gap-1">
            View All <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {categories.map((cat) => (
            <Link
              key={cat.slug}
              href={`/products?category=${cat.slug}`}
              className="group relative overflow-hidden rounded-2xl bg-surface border border-border p-6 hover:border-primary/30 hover:shadow-lg hover:shadow-primary/5 transition-all duration-300"
            >
              <div className={`w-12 h-12 rounded-xl bg-gradient-to-br ${cat.color} flex items-center justify-center mb-4 group-hover:scale-110 transition-transform`}>
                <cat.icon className="w-6 h-6 text-white" />
              </div>
              <h3 className="font-semibold">{cat.name}</h3>
              <ArrowRight className="w-4 h-4 text-muted group-hover:text-primary absolute bottom-6 right-6 group-hover:translate-x-1 transition-all" />
            </Link>
          ))}
        </div>
      </section>

      {/* Shop by Brand */}
      <section className="bg-surface border-y border-border">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 py-12">
          <h2 className="text-2xl font-bold text-center mb-8">Shop by Brand</h2>
          <div className="grid grid-cols-3 md:grid-cols-6 gap-4">
            {brands.map((brand) => (
              <Link
                key={brand.slug}
                href={`/products?brand=${brand.slug}`}
                className="flex items-center justify-center h-16 rounded-xl border border-border bg-background hover:border-primary/30 hover:bg-primary/5 transition-all font-semibold text-muted hover:text-foreground"
              >
                {brand.name}
              </Link>
            ))}
          </div>
        </div>
      </section>

      {/* Features */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 py-16">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          {features.map((feature) => (
            <div key={feature.title} className="text-center">
              <div className="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center mx-auto mb-3">
                <feature.icon className="w-6 h-6 text-primary" />
              </div>
              <h3 className="font-semibold text-sm mb-1">{feature.title}</h3>
              <p className="text-xs text-muted">{feature.description}</p>
            </div>
          ))}
        </div>
      </section>

      {/* CTA */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 pb-16">
        <div className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-primary to-primary-hover p-8 md:p-12 text-white">
          <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGNpcmNsZSBjeD0iMjAiIGN5PSIyMCIgcj0iMSIgZmlsbD0icmdiYSgyNTUsMjU1LDI1NSwwLjA1KSIvPjwvc3ZnPg==')] opacity-50" />
          <div className="relative max-w-lg">
            <h2 className="text-2xl md:text-3xl font-bold mb-3">
              Not sure which phone to pick?
            </h2>
            <p className="text-white/80 mb-6">
              Our AI assistant can help you compare specs, find the best phone for your budget, and answer any questions.
            </p>
            <Link
              href="/ai-chat"
              className="inline-flex items-center gap-2 px-6 py-3 bg-white text-primary rounded-xl font-semibold hover:bg-white/90 transition-all"
            >
              Chat with AI <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        </div>
      </section>
    </>
  );
}
