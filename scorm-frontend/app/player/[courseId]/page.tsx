"use client";

import { useEffect, useState } from "react";
import { getCourseById } from "../../services/scormService";
import { useParams, useRouter } from "next/navigation";

function PlayerPage() {
  const router = useRouter();
  const params = useParams();
  const courseId = params.courseId as string;

  const [course, setCourse] = useState<any>(null);
  useEffect(() => {
    const loadCourse = async () => {
      const data = await getCourseById(courseId);
      setCourse(data.course);
    };

    loadCourse();
  }, [courseId]);

  if (!course) return <div className="p-10">Loading course...</div>;

  return (
    <div className="flex flex-col h-screen">
      <div className="bg-gray-900 text-white p-4 text-lg">
        {course.courseTitle}
      </div>
      <button
        onClick={() => router.push("/courses")}
        className="bg-white text-black px-3 py-1 rounded hover:bg-gray-200"
      >
       ← Back to Courses
      </button>
      <div className="flex-1">
        <iframe src={course.launchUrl} className="w-full h-full border-none" />
      </div>
    </div>
  );
}

export default PlayerPage;
