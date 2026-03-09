const BASE_URL= "http://localhost:8080/scorm";

export const uploadScorm= async(file: File) =>{
    const formData = new FormData();
    formData.append("file", file);
    const response = await fetch(`${BASE_URL}/upload`, {
        method: "POST",
        body: formData,
    }); 
    return response.json();
}

export const getAllCourses = async() => {
    const response = await fetch(`${BASE_URL}/all`);
    return response.json();
}

export const getCourseById = async(id: string) => {
    const response = await fetch(`${BASE_URL}/${id}`);
    return response.json();
}