import Link from "next/link";

export default function Home() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gray-100">
      <main className="text-4xl font-bold mb-10 text-gray-800">
        <h1 className="flex flex-col items-center pb-6">Scrom LMS Demo</h1>
        <div className="flex flex-col gap-6">
          <Link
            href="/upload"
            className="bg-stone-500 text-white flex flex-col items-center px-6 py-3 rounded hover:bg-green-600"
          >
            Upload SCROM
          </Link>
          <Link
            href="/courses"
            className="bg-stone-500 text-white flex flex-col items-center px-6 py-3 rounded hover:bg-green-600"
          >
            View Available Courses
          </Link>
        </div>
      </main>
    </div>
  );
}
