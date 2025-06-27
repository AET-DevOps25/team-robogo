export interface LoginRequestDTO {
  username: string
  password: string
}

export interface UserDTO {
  id: number
  username: string
  email: string
}

export interface LoginResponseDTO {
  success: boolean
  token?: string
  error?: string
}

export interface SessionResponseDTO {
  valid: boolean
  user: UserDTO
  error?: string
}

export interface ScoreDTO {
  points: number
  time: number
  highlight: boolean
}

export interface TeamDTO {
  id: number
  name: string
  rank: number
  scores: ScoreDTO[]
}
