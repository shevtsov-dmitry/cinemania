import { useEffect, useState } from "react";
import Poster from "../poster/Poster";

export default function PreviewCompilation() {
  const COMPILATION_SIZE = 10;

  const [contentDetails, setContentDetails] = useState([]);

  useEffect(() => {
    fetchForMetadata();
  }, []);

  async function fetchForMetadata() {
    const res = await fetch(
      `${STORAGE_SERVER_URL}/v0/api/metadata/recent/${COMPILATION_SIZE}`,
    );
    const resJson = await res.json();
    if (res.status !== 200) {
      console.error("Не удалось загрузить постеры превью.");
    }
    setContentDetails(resJson);
  }

  return (
    <div>
      {contentDetails.length === 0 ? (
        <p>Постеры загружаются</p>
      ) : (
        contentDetails.map((metadata, idx) => {
          <Poster key={idx} metadata={metadata} />;
        })
      )}
    </div>
  );
}