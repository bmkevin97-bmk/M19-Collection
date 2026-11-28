import { createContext, useContext } from 'react'

export type Role = 'USER' | 'ADMIN'

export interface SessionUser {
  id: number
  email: string
  role: Role
}

export interface AuthContextValue {
  user: SessionUser | null
  login: (email: string, password: string) => Promise<void>
  register: (nombre: string, email: string, password: string) => Promise<void>
  logout: () => void
}

export const AuthContext = createContext<AuthContextValue | null>(null)

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used inside AuthProvider')
  return context
}