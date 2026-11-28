import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useNavigate } from 'react-router'
import { ApiError } from '../api/client'
import { useAuth } from '../auth/auth-context'

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [nombre, setNombre] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})
  const [loading, setLoading] = useState(false)

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    setError(null)
    setFieldErrors({})
    setLoading(true)
    try {
      await register(nombre, email, password)
      navigate('/app')
    } catch (err) {
      if (err instanceof ApiError) {
        setError(err.message)
        setFieldErrors(err.fieldErrors ?? {})
      } else {
        setError('Could not reach the server')
      }
    } finally {
      setLoading(false)
    }
  }

  const inputClass =
    'mt-1 w-full rounded-md bg-slate-700 px-3 py-2 outline-none focus:ring-2 focus:ring-orange-400'

  return (
    <main className="min-h-screen bg-slate-900 text-white flex items-center justify-center px-4">
      <form onSubmit={handleSubmit} className="w-full max-w-sm space-y-4 rounded-xl bg-slate-800 p-6 shadow-lg">
        <h1 className="text-2xl font-bold text-orange-400">Create account</h1>

        <label className="block text-sm">
          Name
          <input required value={nombre} onChange={(e) => setNombre(e.target.value)} className={inputClass} />
          {fieldErrors.nombre && <span className="text-xs text-red-400">{fieldErrors.nombre}</span>}
        </label>

        <label className="block text-sm">
          Email
          <input type="email" required value={email} onChange={(e) => setEmail(e.target.value)} className={inputClass} />
          {fieldErrors.email && <span className="text-xs text-red-400">{fieldErrors.email}</span>}
        </label>

        <label className="block text-sm">
          Password (min. 8 characters)
          <input
            type="password"
            required
            minLength={8}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className={inputClass}
          />
          {fieldErrors.password && <span className="text-xs text-red-400">{fieldErrors.password}</span>}
        </label>

        {error && <p className="text-sm text-red-400">{error}</p>}

        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-md bg-orange-500 py-2 font-semibold hover:bg-orange-400 disabled:opacity-50"
        >
          {loading ? 'Creating…' : 'Create account'}
        </button>

        <p className="text-center text-sm text-slate-300">
          Already registered?{' '}
          <Link to="/login" className="text-orange-400 hover:underline">
            Sign in
          </Link>
        </p>
      </form>
    </main>
  )
}