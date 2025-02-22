import Constants from "@/src/constants/Constants";
import useFormAddCreatorStore from "@/src/state/formAddCreatorState";
import ContentDetails from "@/src/types/ContentMetadata";
import PersonCategory from "@/src/types/PersonCategory";
import UserPic from "@/src/types/UserPic";
import React, { FormEvent, useEffect, useRef, useState } from "react";

const FormAddCreator = () => {
  const [name, setName] = useState("");
  const [nameLatin, setNameLatin] = useState("");
  const [surname, setSurname] = useState("");
  const [surnameLatin, setSurnameLatin] = useState("");
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

  interface OperationStatus {
    ok: boolean;
    message?: string;
  }

  const [operationStatus, setOperationStatus] = useState<
    OperationStatus | undefined
  >();

  const formRef = useRef<HTMLFormElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const hideFormAddCreator = useFormAddCreatorStore(
    (state) => state.hideFormAddCreator
  );

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();

    const userPicMetadata = await uploadImage();

    const newCreator = {
      name,
      surname,
      nameLatin,
      surnameLatin,
      bornPlace,
      heightMeters,
      age,
      userPic: userPicMetadata as UserPic,
      birthDate: parseDateEngToRus(birthDate),
      deathDate: isDead ? parseDateEngToRus(deathDate) : null,
      isDead,
    };

    const res = await fetch(
      Constants.STORAGE_URL + "/api/v0/metadata/content-creators",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(newCreator),
      }
    );

    if (!res.ok) {
      const errmes = decodeURI(res.headers.get("Message") as string).replaceAll(
        "+",
        " "
      );
      setOperationStatus({ ok: false, message: errmes });
      console.error(errmes);
    } else {
      setOperationStatus({ ok: true, message: "✓" });
    }
  }

  function parseDateEngToRus(date: string): string {
    const dateObj = new Date(date);
    const options: Intl.DateTimeFormatOptions = {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    };

    return dateObj.toLocaleDateString("ru-RU", options);
  }

  async function uploadImage(): Promise<UserPic | undefined> {
    const file = fileInputRef.current?.files?.[0];
    if (file) {
      const formData = new FormData();
      formData.append("image", file);
      formData.append("personCategory", personCategory as string);

      const res = await fetch(
        Constants.STORAGE_URL +
          "/api/v0/metadata/content-creators/user-pics/upload",
        {
          method: "POST",
          body: formData,
        }
      );

      if (!res.ok) {
        const errmes: string = decodeURI(
          res.headers.get("Message") as string
        ).replaceAll("+", " ");
        setOperationStatus({ ok: false, message: errmes });
        console.error(errmes);
        return;
      }

      return await res.json();
    }
  }

  function resetForm() {
    setName("");
    setNameLatin("");
    setSurname("");
    setSurnameLatin("");
    setBornPlace("");
    setHeightMeters(0);
    setAge(0);
    setBirthDate("");
    setDeathDate("");
    setIsDead(false);
    setFilmsParticipated([]);
    if (formRef.current) formRef.current.reset();
  }

  useEffect(() => {
    if (!operationStatus) return;

    const timer = setTimeout(() => {
      setOperationStatus(undefined);
    }, 3000);
    return () => clearTimeout(timer);
  }, [operationStatus]);

  return (
    <div className="flex min-h-screen w-full items-center justify-center bg-gray-100 p-4 dark:bg-gray-900">
      <form
        ref={formRef}
        className="relative max-w-md rounded-lg bg-white p-6 shadow-lg full dark:bg-neutral-800 dark:text-blue-100"
        onSubmit={handleSubmit}
      >
        <div id="close-sign" className="absolute top-4 right-4">
          <button
            type="button"
            className="cursor-pointer select-none text-xl font-bold text-gray-500 hover:text-gray-700 dark:text-blue-200 dark:hover:text-blue-300"
            onClick={hideFormAddCreator}
          >
            &#10006;
          </button>
        </div>

        <h2 className="mb-6 text-center text-3xl font-bold text-gray-900 dark:text-blue-100">
          Добавить члена съёмочной группы
        </h2>

        <ul className="space-y-4">
          <li className="flex gap-3">
            <div>
              <label className="mb-1 block text-sm font-medium">Имя</label>
              <input
                className="w-full rounded-md border px-3 py-2 dark:bg-neutral-700"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Введите имя"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium">Фамилия</label>
              <input
                className="w-full rounded-md border px-3 py-2 dark:bg-neutral-700"
                value={surname}
                onChange={(e) => setSurname(e.target.value)}
                placeholder="Введите фамилию"
              />
            </div>
          </li>

          <li className="flex gap-3">
            <div>
              <label className="mb-1 block text-sm font-medium">
                Имя (Латиницей)
              </label>
              <input
                className="w-full rounded-md border px-3 py-2 dark:bg-neutral-700"
                value={nameLatin}
                onChange={(e) => setNameLatin(e.target.value)}
                placeholder="Введите имя латиницей"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium">
                Фамилия (Латиницей)
              </label>
              <input
                className="w-full rounded-md border px-3 py-2 dark:bg-neutral-700"
                value={surnameLatin}
                onChange={(e) => setSurnameLatin(e.target.value)}
                placeholder="Введите Фамилию латиницей"
              />
            </div>
          </li>

          <li>
            <label className="mb-1 block text-sm font-medium">
              Место рождения
            </label>
            <input
              className="w-full rounded-md border px-3 py-2 dark:bg-neutral-700"
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
              <label className="mb-1 block text-sm font-medium">Рост (м)</label>
              <input
                className="w-full rounded-md border px-3 py-2 dark:bg-neutral-700"
                type="number"
                value={heightMeters}
                onChange={(e) =>
                  setHeightMeters(parseFloat(e.target.value) || 0)
                }
                placeholder="1.82"
              />
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium">Возраст</label>
              <input
                className="w-full rounded-md border px-3 py-2 dark:bg-neutral-700"
                type="number"
                value={age}
                onChange={(e) => setAge(parseInt(e.target.value, 10) || 0)}
              />
            </div>
          </li>

          <li className="flex items-center gap-2">
            <div className="flex gap-1 mb-[-20px]">
              <label className="mb-1 block text-sm font-medium">
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
              <label className="mb-1 block text-sm font-medium">
                Дата рождения
              </label>
              <input
                className="w-full rounded-md border px-3 py-2 dark:bg-neutral-700"
                type="date"
                value={birthDate}
                onChange={(e) => setBirthDate(e.target.value)}
              />
            </div>

            {isDead && (
              <div>
                <label className="mb-1 block text-sm font-medium">
                  Дата смерти
                </label>
                <input
                  className="w-full rounded-md border px-3 py-2"
                  type="date"
                  value={deathDate}
                  onChange={(e) => setDeathDate(e.target.value)}
                />
              </div>
            )}
          </li>

          <li>
            <button
              className="rounded bg-neutral-100 px-4 py-2 font-bold text-black shadow hover:bg-neutral-200"
              type="button"
            >
              Выбрать связанные шоу
            </button>
          </li>

          <li>
            <label className="mb-1 block text-sm font-medium">Фото</label>
            <input
              ref={fileInputRef}
              type="file"
              className="block w-full text-sm"
              accept="image/*"
            />
          </li>

          {/* TODO create image picker */}
          {/*{userPic && (*/}
          {/*    <li>*/}
          {/*        <img*/}
          {/*            src={userPic.filename}*/}
          {/*            alt="Загруженное изображение"*/}
          {/*            className="mt-2 h-32 w-32 border"*/}
          {/*        />*/}
          {/*    </li>*/}
          {/*)}*/}
        </ul>

        <div className="mt-6 flex justify-center gap-4">
          <div className={"absolute left-5"}>
            {operationStatus && (
              <p
                className={
                  operationStatus.ok
                    ? "text-green-500 text-4xl"
                    : "text-red-500"
                }
              >
                {operationStatus?.message}
              </p>
            )}
          </div>
          <button
            type="submit"
            className="rounded-lg bg-blue-600 px-4 py-2 font-bold text-white shadow hover:bg-blue-700 focus:outline-none"
          >
            Сохранить
          </button>
          <button
            type="button"
            onClick={resetForm}
            className="rounded-lg bg-neutral-100 px-4 py-2 font-bold text-black shadow hover:bg-neutral-200"
          >
            Очистить
          </button>
        </div>
      </form>
    </div>
  );
};

export default FormAddCreator;
