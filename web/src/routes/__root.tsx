import { createRootRoute, Link, Outlet } from '@tanstack/react-router'
import { TanStackRouterDevtools } from '@tanstack/react-router-devtools'
import { getUser, logout } from '../account'

function RootLayout() {
  const navigate = Route.useNavigate()

  function loginout() {
    if (getUser() == null) navigate({ to: "/login" })
    else {
      logout()
      window.location.reload()
    }
  }

  return <>
    <div style={{ display: "flex", justifyContent: "space-between" }}>
      <div>
        <Link to="/">
          Jetstream Judge
        </Link>{' '}
        <Link to="/problems">
          Problems
        </Link>{' '}
        <Link to="/submissions">
          Submissions
        </Link>
      </div>
      <div>
        {getUser() != null ? `Hello ${getUser()} ` : ""}
        <button onClick={loginout}>{getUser() != null ? "Log out" : "Log in"}</button>
      </div>
    </div>
    <hr />
    <Outlet />
    <TanStackRouterDevtools />
  </>
}

export const Route = createRootRoute({ component: RootLayout })