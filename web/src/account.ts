export async function login(user: string, password: string): Promise<boolean> {
  const ok = (await fetch("/api/login", {
    method: "POST",
    headers: {
      "Authorization": `${user}:${password}`
    }
  })).ok

  if (ok) {
    localStorage.setItem("auth", `${user}:${password}`)
  }

  return ok
}

export function logout() {
  localStorage.removeItem("auth")
}

export function getAuth(): string {
  const auth = localStorage.getItem("auth")
  return auth || ""
}

export function getUser(): string | null {
  const auth = localStorage.getItem("auth")
  if (auth == null) return null
  return auth.split(":")[0]
}
