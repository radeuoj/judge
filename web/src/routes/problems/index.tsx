import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useState, type ReactNode } from "react";
import { fetchApi } from "../../api";

export const Route = createFileRoute("/problems/")({
  component: Problems,
})

type Problem = {
  id: number
  name: string
  score: number
  tests: number
}

function makeTableRows(problems: Problem[]): ReactNode[] {
  return problems.map(p => <tr key={p.id}>
      <td>{p.id}</td>
      <td><Link to={`/problems/$problemId`} params={{ problemId: p.id.toString() }}>{p.name}</Link></td>
  </tr>)
}

function Problems() {
  const [problems, setProblems] = useState<Problem[]>([])
  
  useEffect(() => {
    fetchApi("/problems").then(async (res) => {
      setProblems(await res.json())
    })
  }, [])

  return <div>
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Problem</th>
          {/* <th>Score</th> */}
        </tr>
      </thead>
      <tbody>
        {makeTableRows(problems)}
      </tbody>
    </table>
  </div>
}