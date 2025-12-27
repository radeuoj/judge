import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useState, type ReactNode } from "react";

export const Route = createFileRoute("/submissions/")({
  component: Submissions,
})

export type Submission = {
  id: number
  userId: number
  userName: string
  problemId: number
  problemName: string
  status: "NOTHING" | "COMPILER_ERROR" | "EVALUATED"
  score: number
  tests: number
}

function makeTableRows(submissions: Submission[]): ReactNode[] {
  return submissions.map((s) => <tr key={s.id}>
    <td><Link to="/submissions/$submissionId" params={{ submissionId: `${s.id}` }}>{s.id}</Link></td>
    <td>{s.userName}</td>
    <td>{s.problemName}</td>
    <td>{s.status}</td>
    <td>{s.score}/{s.tests}</td>
  </tr>)
}

function Submissions() {
  const [submissions, setSubmissions] = useState<Submission[]>([])

  useEffect(() => {
    fetch("/api/submissions").then(async (res) => {
      setSubmissions(await res.json())
    })
  }, [])

  return <div>
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>User</th>
          <th>Problem</th>
          <th>Status</th>
          <th>Score</th>
        </tr>
      </thead>
      <tbody>
        {makeTableRows(submissions)}
      </tbody>
    </table>
  </div>
}