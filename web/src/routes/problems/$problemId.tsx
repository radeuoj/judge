import { createFileRoute } from "@tanstack/react-router";
import { useEffect, useRef, useState } from "react";
import { getAuth } from "../../account";

export const Route = createFileRoute("/problems/$problemId")({
  component: Problem,
})

function Problem() {
  const { problemId } = Route.useParams()
  const [statement, setStatement] = useState("")
  const inputRef = useRef<HTMLInputElement | null>(null)

  useEffect(() => {
    fetch(`/api/problems/${problemId}/statement`).then(async (res) => {
      setStatement(await res.text())
    })
  }, [])

  async function submit() {
    if (inputRef.current?.files == null || inputRef.current?.files.length == 0) return
    const code = await inputRef.current?.files[0].text()

    fetch(`/api/problems/${problemId}/submit`, {
      method: "POST",
      body: code,
      headers: {
        "Authorization": getAuth(),
      }
    })
  }

  return <div>
    <div style={{ whiteSpace: "pre-wrap" }}>{statement}</div>
    <br/>
    <label htmlFor="submit">Submit code:</label>
    <input ref={inputRef} type="file" id="submit" name="submit" /><br/><br/>
    <button onClick={submit}>Submit</button>
  </div>
}