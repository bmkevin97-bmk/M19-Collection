export interface AuthResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
}

export type EstadoColeccion = 'COLECCION' | 'WISHLIST'

export interface ColeccionItem {
  id: number
  usuarioId: number
  varianteId: number
  modelo: string
  color: string
  edicion: string
  estado: EstadoColeccion
  cantidad: number
}