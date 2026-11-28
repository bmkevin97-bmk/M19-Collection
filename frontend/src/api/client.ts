const API_URL: string = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'
const TOKEN_KEY = 'm19_token'

export const tokenStorage = {
  get: () => localStorage.getItem(TOKEN_KEY),
  set: (token: string) => localStorage.setItem(TOKEN_KEY, token),
  clear: () => localStorage.removeItem(TOKEN_KEY),
}

export class ApiError extends Error {
  status: number
  fieldErrors?: Record<string, string>

  constructor(status: number, message: string, fieldErrors?: Record<string, string>) {
    super(message)
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

interface RequestOptions {
  method?: string
  body?: unknown
  auth?: boolean
}

export async function request<T>(
  path: string,
  { method = 'GET', body, auth = true }: RequestOptions = {},
): Promise<T> {
  const headers: Record<string, string> = {}
  if (body !== undefined) headers['Content-Type'] = 'application/json'

  const token = tokenStorage.get()
  if (auth && token) headers['Authorization'] = `Bearer ${token}`

  const response = await fetch(`${API_URL}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  })

  if (response.status === 401 && auth) {
    tokenStorage.clear()
    window.dispatchEvent(new Event('auth:expired'))
  }

  if (!response.ok) {
    let message = response.statusText
    let fieldErrors: Record<string, string> | undefined
    try {
      const data = await response.json()
      message = data.message ?? message
      fieldErrors = data.errors
    } catch {
      // the body was empty or not JSON
    }
    throw new ApiError(response.status, message, fieldErrors)
  }

  if (response.status === 204) return undefined as T
  return (await response.json()) as T
}