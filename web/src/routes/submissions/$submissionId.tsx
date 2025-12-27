import { createFileRoute } from '@tanstack/react-router'
import type { Submission } from '.'
import { useEffect, useState, type ReactNode } from 'react'
import { common, createStarryNight } from '@wooorm/starry-night'
import { toJsxRuntime } from 'hast-util-to-jsx-runtime'
import { Fragment, jsx, jsxs } from 'react/jsx-runtime'

// import "@wooorm/starry-night/style/both"

export const Route = createFileRoute('/submissions/$submissionId')({
  component: SubmissionDetails,
})

type Test = {
  test: number
  time: number
  status: "UNEVALUATED" | "ERROR" | "WRONG_ANSWER" | "ACCEPTED"
  error: string | null
}

type BigSubmission = {
  submission: Submission
  tests: Test[]
  code: string
}

function SubmissionDetails() {
  const { submissionId } = Route.useParams()
  const [submission, setSubmission] = useState<BigSubmission | null>(null)
  const [code, setCode] = useState<ReactNode>()
  
  useEffect(() => {
    fetch(`/api/submissions/${submissionId}`).then(async (res) => {
      const s: BigSubmission = await res.json()
      setSubmission(s)

      console.log(s.code)
      const starryNight = await createStarryNight(common)
      const tree = starryNight.highlight(s.code, "source.c")
      console.dir(tree)
      setCode(toJsxRuntime(tree, { Fragment, jsx, jsxs }))
    })
  }, [])

  return <div>
    <p>{JSON.stringify(submission)}</p>
    <pre>{code}</pre>
  </div>
}
