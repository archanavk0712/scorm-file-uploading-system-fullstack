"use client";
import React, { useEffect, useState } from "react";
import { getAllCourses } from "../services/scormService";
import { useRouter } from "next/navigation";

function CoursePage() {
  const [courses, setCourses] = useState<any[]>([]);
  const router = useRouter();

  useEffect(() => {
    const loadCourses = async () => {
      const data = await getAllCourses();
      setCourses(data.courseList);
    };

    loadCourses();
  }, []);

  return (
    <div className="p-10">
      <h1 className="text-2xl font-bold mb-6">Available SCORM Courses</h1>
      <button
        onClick={() => router.push("/")}
        className="bg-white text-black px-3 py-1 rounded hover:bg-gray-200"
      >
       ← Back to Home page
      </button>
      <div className="grid grid-4">
        {courses.map((course) => (
          <div
            key={course.id}
            onClick={() => router.push(`/player/${course.id}`)}
            className="border p-4 rounded cursor-pointer hover:bg-gray-100"
          >
            <h2 className="text-lg font-semibold">{course.courseTitle}</h2>
            <p className="text-gray-500">{course.version}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default CoursePage;
