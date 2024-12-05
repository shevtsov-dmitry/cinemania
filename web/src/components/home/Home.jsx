import Preview from "@/src/components/home/Preview";
import DefaultCompilation from "@/src/components/common/compilations/DefaultCompilation";

export default function Home() {
  return (
    <div>
      <h3 className={"p-2 text-2xl font-bold text-white"}>Новинки</h3>
      <Preview />
      <h3 className={"p-2 text-2xl font-bold text-white"}>
        Вам может понравится
      </h3>
      <DefaultCompilation />
    </div>
  );
}
