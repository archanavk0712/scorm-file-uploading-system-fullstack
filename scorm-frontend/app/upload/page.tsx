"use client";
import { useRouter } from "next/navigation";
import { uploadScorm } from "../services/scormService";
import { useState } from "react";

function UploadPage() {
  const router = useRouter();
  const [fileName, setFileName] = useState("Choose SCORM zip file");
  const handleUpload = async (event: any) => {
    const file = event.target.files[0];
    if (!file) return;
    setFileName(file.name);
    await uploadScorm(file);
    alert("SCORM updated successfully");
    router.push("/courses");
  };
  return (
    <div className="flex flex-col items-center justify-center h-screen gap-6">
      <h1 className="text-2xl font-bold">Upload SCORM Package</h1>
      <label className="w-80 border rounded p-3 bg-white cursor-pointer text-center">
        {fileName}
        <input
          type="file"
          accept=".zip"
          onChange={handleUpload}
          className="hidden"
        />
      </label>
       <button
        onClick={() => router.push("/")}
        className="bg-white text-black px-3 py-1 rounded hover:bg-gray-200"
      >
       ← Back to Home page
      </button>
    </div>
  );
}

export default UploadPage;
