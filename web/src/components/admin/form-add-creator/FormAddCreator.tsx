import Constants from "@/src/constants/Constants";
import useFormAddCreatorStore from "@/src/state/formAddCreatorState";
import ContentDetails from "@/src/types/ContentDetails";
import PersonCategory from "@/src/types/PersonCategory";
import UserPic from "@/src/types/UserPic";
import React, { useState, useRef, FormEventHandler, useEffect } from "react";

const FormAddCreator = () => {
  const [fullname, setFullname] = useState("");
  const [fullnameEng, setFullnameEng] = useState("");
  const [bornPlace, setBornPlace] = useState("");
  const [heightMeters, setHeightMeters] = useState(0);
  const [age, setAge] = useState(0);
  const [birthDate, setBirthDate] = useState("");
  const [deathDate, setDeathDate] = useState("");
  const [personCategory, setPersonCategory] = useState<PersonCategory>();
  const [filmsParticipated, setFilmsParticipated] = useState<ContentDetails[]>(
    []
  );
  const [isDead, setIsDead] = useState(false);
  const [userPic, setUserPic] = useState<UserPic | null>();

  const formRef = useRef<HTMLFormElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const hideFormAddCreator = useFormAddCreatorStore(
    (state) => state.hideFormAddCreator
  );

  async function handleSubmit(e: FormEventHandler<HTMLFormElement>) {
    // e.preventDefault();

    const newCreator = {
      fullname,
      fullnameEng,
      bornPlace,
      heightMeters,
      age,
      userPic,
      birthDate,
      deathDate: isDead ? deathDate : null,
      isDead,
    };

    const res = await fetch(Constants.STORAGE_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(newCreator),
    });

    if (!res.ok) {
      throw new Error("Failed to add creator.");
    }
  }

  async function uploadImage() {
    const file = fileInputRef.current?.files?.[0];
    if (file) {
      const formData = new FormData();
      formData.append("image", file);
      formData.append("personCategory", personCategory as string);

      const res = await fetch(
        Constants.STORAGE_URL + "/api/v0/content-creators/user-pics/upload",
        {
          method: "POST",
          body: formData,
        }
      );

      if (!res.ok) {
        const errmes: string = decodeURI(
          res.headers.get("Message") as string
        ).replaceAll("+", " ");
        console.error(errmes);
        return;
      }

      const data = await res.json();
      setUserPic(data);
    }
  }

  function resetForm() {
    setFullname("");
    setFullnameEng("");
    setBornPlace("");
    setHeightMeters(0);
    setAge(0);
    setBirthDate("");
    setDeathDate("");
    setIsDead(false);
    setUserPic(null);
    if (formRef.current) formRef.current.reset();
  }

  return (
    <div className="flex min-h-screen w-full items-center justify-center bg-gray-100 dark:bg-gray-900 p-4">
      <form
        ref={formRef}
        className="relative full max-w-md rounded-lg bg-white p-6 shadow-lg dark:bg-neutral-800 dark:text-blue-100"
        onSubmit={handleSubmit}
      >
        <div className="absolute top-4 right-4">
          <button
            type="button"
            className="cursor-pointer select-none text-xl font-bold text-gray-500 hover:text-gray-700 dark:text-blue-200 dark:hover:text-blue-300"
            onClick={hideFormAddCreator}
          >
            X
          </button>
        </div>

        <h2 className="mb-6 text-center text-3xl font-bold text-gray-900 dark:text-blue-100">
          Добавить члена съёмочной группы
        </h2>

        <ul className="space-y-4">
          <li>
            <label className="block text-sm font-medium mb-1">Полное имя</label>
            <input
              className="w-full rounded-md border py-2 px-3"
              value={fullname}
              onChange={(e) => setFullname(e.target.value)}
              placeholder="Введите полное имя"
            />
          </li>

          <li>
            <label className="block text-sm font-medium mb-1">
              Полное имя (Латиницей)
            </label>
            <input
              className="w-full rounded-md border py-2 px-3"
              value={fullnameEng}
              onChange={(e) => setFullnameEng(e.target.value)}
              placeholder="Введите имя латиницей"
            />
          </li>

          <li>
            <label className="block text-sm font-medium mb-1">
              Место рождения
            </label>
            <input
              className="w-full rounded-md border py-2 px-3"
              value={bornPlace}
              onChange={(e) => setBornPlace(e.target.value)}
              placeholder="Введите место рождения"
            />
          </li>

          <li>
            <label>Должность</label>
            <div className="flex gap-1">

            {Object.values(PersonCategory).map((category) => (
              <div key={category}>
                <input
                  type="radio"
                  value={category}
                  checked={personCategory === category}
                  onChange={(e) =>
                    setPersonCategory(e.target.value as PersonCategory)
                  }
                />
                <label className="ml-2">{category}</label>
              </div>
            ))}
            </div>
          </li>

          <li className="flex gap-2">
            <div>
              <label className="block text-sm font-medium mb-1">Рост (м)</label>
              <input
                className="w-full rounded-md border py-2 px-3"
                type="number"
                value={heightMeters}
                onChange={(e) =>
                  setHeightMeters(parseFloat(e.target.value) || 0)
                }
                placeholder="1.82"
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Возраст</label>
              <input
                className="w-full rounded-md border py-2 px-3"
                type="number"
                value={age}
                onChange={(e) => setAge(parseInt(e.target.value, 10) || 0)}
              />
            </div>
          </li>

          <li className="flex gap-2 items-center ">
            <div className="flex gap-1 mb-[-20px]">
              <label className="block text-sm font-medium mb-1">
                {"Мёртв"}
              </label>
              <input
                className="mt-[-2px]"
                type="checkbox"
                checked={isDead}
                onChange={(e) => setIsDead(e.target.checked)}
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">
                Дата рождения
              </label>
              <input
                className="w-full rounded-md border py-2 px-3"
                type="date"
                value={birthDate}
                onChange={(e) => setBirthDate(e.target.value)}
              />
            </div>

            {isDead && (
              <div>
                <label className="block text-sm font-medium mb-1">
                  Дата смерти
                </label>
                <input
                  className="w-full rounded-md border py-2 px-3"
                  type="date"
                  value={deathDate}
                  onChange={(e) => setDeathDate(e.target.value)}
                />
              </div>
            )}
          </li>

          <li>
            <label className="block text-sm font-medium mb-1">Фото</label>
            <input
              ref={fileInputRef}
              type="file"
              className="block w-full text-sm"
              accept="image/*"
            />
          </li>

          {userPic && (
            <li>
              <img
                src={userPic.filename}
                alt="Загруженное изображение"
                className="mt-2 w-32 h-32 border"
              />
            </li>
          )}
        </ul>

        <div className="mt-6 flex justify-center gap-4">
          <button
            type="submit"
            className="rounded-lg bg-blue-600 px-4 py-2 font-bold text-white hover:bg-blue-700 focus:outline-none"
          >
            Сохранить
          </button>
          <button
            type="button"
            onClick={resetForm}
            className="rounded-lg bg-gray-300 px-4 py-2 font-bold text-black hover:bg-gray-400"
          >
            Очистить
          </button>
        </div>
      </form>
    </div>
  );
};

export default FormAddCreator;
