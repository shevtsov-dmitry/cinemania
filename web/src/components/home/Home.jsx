import Preview from "@/src/components/home/Preview";
import DefaultCompilation from "@/src/components/common/compilations/DefaultCompilation";
import { useState } from "react";

export default function Home() {
  return (
    <div>
      <h3 className={"p-2 text-2xl font-bold text-white"}>Новинки</h3>
      <Preview />
      <h3 className={"p-2 text-2xl font-bold text-white"}>
        Вам может понравится
      </h3>
      <div id="preview-posters-holder"></div>
      <DefaultCompilation metadataList={null} />
    </div>
  );
}
