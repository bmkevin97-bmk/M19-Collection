import { useEffect, useState } from 'react'
import { ApiError, request } from '../api/client'
import type { ColeccionItem } from '../api/type'
import { useAuth } from '../auth/auth-context'

export default function CollectionPage() {
  const { user, logout } = useAuth()
  const [items, setItems] = useState<ColeccionItem[] | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    request<ColeccionItem[]>('/api/me/coleccion')
      .then(setItems)
      .catch((err) =>
        setError(err instanceof ApiError ? err.message : 'Could not load your collection'),
      )
  }, [])

  return (
    <main className="min-h-screen bg-slate-900 text-white">
      <header className="flex items-center justify-between border-b border-slate-700 px-6 py-4">
        <h1 className="text-xl font-bold text-orange-400">M19 Collection</h1>
        <div className="flex items-center gap-4 text-sm">
          <span className="text-slate-300">{user?.email}</span>
          <button onClick={logout} className="rounded-md bg-slate-700 px-3 py-1 hover:bg-slate-600">
            Sign out
          </button>
        </div>
      </header>

      <section className="mx-auto max-w-3xl p-6">
        <h2 className="mb-4 text-lg font-semibold">My collection</h2>

        {error && <p className="text-red-400">{error}</p>}
        {!error && items === null && <p className="text-slate-400">Loading…</p>}
        {items?.length === 0 && <p className="text-slate-400">Your collection is empty.</p>}

        <ul className="space-y-2">
          {items?.map((item) => (
            <li key={item.id} className="flex items-center justify-between rounded-lg bg-slate-800 px-4 py-3">
              <div>
                <p className="font-medium">{item.modelo}</p>
                <p className="text-sm text-slate-400">
                  {item.color} · {item.edicion}
                </p>
              </div>
              <span className="rounded-full bg-orange-500/20 px-3 py-1 text-sm text-orange-300">
                × {item.cantidad}
              </span>
            </li>
          ))}
        </ul>
      </section>
    </main>
  )
}