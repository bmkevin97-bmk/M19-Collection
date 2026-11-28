import { Link, Navigate } from 'react-router'
import { useAuth } from '../auth/auth-context'

export default function HomePage() {
  const { user } = useAuth()
  if (user) return <Navigate to="/app" replace />

  return (
    <main className="min-h-screen bg-slate-900 text-white flex flex-col items-center justify-center gap-6 px-4 text-center">
      <h1 className="text-4xl font-bold text-orange-400">M19 Collection</h1>
      <p className="max-w-md text-slate-300">
        Manage your Hot Wheels collection, wishlist and purchases in one place.
      </p>
      <div className="flex gap-3">
        <Link to="/login" className="rounded-md bg-orange-500 px-5 py-2 font-semibold hover:bg-orange-400">
          Sign in
        </Link>
        <Link to="/register" className="rounded-md bg-slate-700 px-5 py-2 font-semibold hover:bg-slate-600">
          Create account
        </Link>
      </div>
    </main>
  )
}