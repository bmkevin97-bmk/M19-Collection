import { useCallback, useEffect, useMemo, useState } from 'react'
import type { ReactNode } from 'react'
import { request, tokenStorage } from '../api/client'
import type { AuthResponse } from '../api/types'
import { AuthContext } from './auth-context'
import type { SessionUser } from './auth-context'

function parseToken(token: string): SessionUser | null {
  try {
    const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')))
    if (payload.exp * 1000 < Date.now()) return null
    return { id: Number(payload.sub), email: payload.email, role: payload.role ?? 'USER' }
  } catch {
    return null
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => {
    const stored = tokenStorage.get()
    if (stored && parseToken(stored)) return stored
    tokenStorage.clear()
    return null
  })

  const user = useMemo(() => (token ? parseToken(token) : null), [token])

  useEffect(() => {
    const onExpired = () => setToken(null)
    window.addEventListener('auth:expired', onExpired)
    return () => window.removeEventListener('auth:expired', onExpired)
  }, [])

  const login = useCallback(async (email: string, password: string) => {
    const response = await request<AuthResponse>('/api/auth/login', {
      method: 'POST',
      body: { email, password },
      auth: false,
    })
    tokenStorage.set(response.accessToken)
    setToken(response.accessToken)
  }, [])

  const register = useCallback(
    async (nombre: string, email: string, password: string) => {
      await request('/api/auth/register', {
        method: 'POST',
        body: { nombre, email, password },
        auth: false,
      })
      await login(email, password)
    },
    [login],
  )

  const logout = useCallback(() => {
    tokenStorage.clear()
    setToken(null)
  }, [])

  const value = useMemo(
    () => ({ user, login, register, logout }),
    [user, login, register, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}