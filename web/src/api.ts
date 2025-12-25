export function fetchApi(path: string): Promise<Response> {
  // return fetch(`http://localhost:8080/api${path}`)
  return fetch(`/api${path}`)
}