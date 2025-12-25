import { createFileRoute } from '@tanstack/react-router'
import { useRef, useState } from 'react'
import { login } from '../account'

export const Route = createFileRoute('/login')({
  component: Login,
})

function Login() {
  const userInputRef = useRef<HTMLInputElement | null>(null)
  const passwordInputRef = useRef<HTMLInputElement | null>(null)
  const navigate = Route.useNavigate()
  const [error, setError] = useState("")

  async function onLogin() {
    const ok = await login(userInputRef.current?.value!, passwordInputRef.current?.value!)
    if (ok) {
      navigate({ to: "/", reloadDocument: true })
    } else setError("Unauthorized")
  }

  return <div>
    <label htmlFor='user'>User </label>
    <input ref={userInputRef} name='user'/><br/>
    <label htmlFor='password'>Password </label>
    <input ref={passwordInputRef} name='password' type="password"/><br/>
    <button onClick={onLogin}>Log in</button>
    <div style={{ color: "red" }}>{error}</div>
  </div>
}
