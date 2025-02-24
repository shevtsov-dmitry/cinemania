import Constants from "@/src/constants/Constants";
import useFilmCrewChooserStore from "@/src/state/formFilmCrewChooserState";
import ContentCreator from "@/src/types/ContentCreator";
import PersonCategory from "@/src/types/PersonCategory";
import React, {Dispatch, FormEvent, ReactElement, SetStateAction, useEffect, useState,} from "react";

const FormFilmCrewChooser = (): ReactElement => {
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [creators, setCreators] = useState<ContentCreator[]>([]);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [creatorTypeHeaderLabel, setCreatorTypeHeaderLabel] =
    useState<string>();

  const hideFilmCrewChooser = useFilmCrewChooserStore((state) => state.hide);

  const creatorType: PersonCategory = useFilmCrewChooserStore(
    (state) => state.choosingType,
  );
  const selectedActorsList: ContentCreator[] = useFilmCrewChooserStore(
    (state) => state.selectedActors,
  );

  const selectedDirector = useFilmCrewChooserStore(state => state.selectedDirector) as ContentCreator

  useEffect(() => {
    if (creatorType === PersonCategory.DIRECTOR) {
      setCreatorTypeHeaderLabel("Выбор режиссёра");
    } else if (creatorType === PersonCategory.ACTOR) {
      setCreatorTypeHeaderLabel("Выбор актёров");
    }
  }, [creatorType]);

  const handleSearch = async (e: FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);
    if (!searchTerm.trim()) {
      setErrorMessage("Введите имя или фамилию для поиска.");
      return;
    }

    let response
    try {
      response = await fetch(
        Constants.STORAGE_URL +
          `/api/v0/metadata/content-creators/fullname/${encodeURIComponent(
            searchTerm,
          )}`,
      );

      if (!response.ok) {
        throw new Error()
      }

      const data: ContentCreator[] = await response.json();
      setCreators(data);
    } catch (error) {
      if (response) {
        const errmes: string = decodeURI(response.headers.get("Message") || "Ошибка при поиске").replaceAll("+", " ")
        setErrorMessage(errmes)
      } else {
        setErrorMessage("Ошибка при поиске");
      }
    }
  };

  return (
    <div className="related p-5 bg-white text-gray-800 max-w-2xl mx-auto shadow rounded">
      <div
        id="close-sign"
        className="absolute top-1 right-2 hover:cursor-pointer"
        onClick={hideFilmCrewChooser}
      >
        ✖
      </div>
      <h1 className="font-bold">{creatorTypeHeaderLabel}</h1>
      <div className="flex gap-2 mb-5 mt-1">
        <label htmlFor="search-input" className="sr-only">
          Поиск по имени\фамилии
        </label>
        <input
          id="search-input"
          type="text"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="Поиск по имени или фамилии"
          className="p-2 w-72 border-2 border-blue-500 rounded text-base"
        />
        <button
          onClick={handleSearch}
          className="p-2 bg-blue-500 text-white rounded cursor-pointer hover:bg-blue-700"
        >
          Искать
        </button>
      </div>
      {errorMessage && <div className="text-red-500 mb-5">{errorMessage}</div>}
      {selectedDirector && creatorType === PersonCategory.DIRECTOR && (
          <h1>Выбранный режиссёр: {selectedDirector.name} {selectedDirector.surname}</h1>
      )}
      {creatorType === PersonCategory.ACTOR && selectedActorsList.length > 0 && (
        <div className={"flex gap-2"}>
          <h1>Выбранные актёры:</h1>
          <div className={"flex"}>
            {selectedActorsList.map((actor, idx) => (
              <div
                key={actor.id}
                className="flex items-center gap-2 flex-wrap text-sm text-neutral-500"
              >
                {actor.name} {actor.surname} {idx !== selectedActorsList.length - 1 && ","}
              </div>
            ))}
          </div>
        </div>
      )}
      <div className="grid grid-cols-2 gap-5">
        {creators.length > 0 &&
          creators
              .filter(creator => creator.personCategory === creatorType)
              .map((creator) => (
            <CreatorItem
              key={creator.id}
              creator={creator}
              setCreators={setCreators}
            />
          ))}
      </div>
    </div>
  );
};

interface CreatorItemProps {
  creator: ContentCreator;
  setCreators: Dispatch<SetStateAction<ContentCreator[]>>;
}

const CreatorItem = ({
  creator,
  setCreators,
}: CreatorItemProps): ReactElement => {
  const [imageUrl, setImageUrl] = useState<string | null>(null);
  const setDirector = useFilmCrewChooserStore(
    (state) => state.setSelectedDirector,
  );
  const setActors = useFilmCrewChooserStore((state) => state.setSelectedActors);
  const actors: ContentCreator[] = useFilmCrewChooserStore(
    (state) => state.selectedActors,
  );

  useEffect(() => {
    const fetchImage = async () => {
      try {
        const response = await fetch(
          Constants.STORAGE_URL +
            `/api/v0/metadata/content-creators/user-pics/${creator.personCategory}/${creator.userPic.id}`,
        );
        if (!response.ok) {
          throw new Error(`Ошибка ${response.status} при загрузке изображения.`);
        }
        const blob = await response.blob();
        const url = URL.createObjectURL(blob);
        setImageUrl(url);
      } catch (error) {
        console.error("Error fetching image:", error);
      }
    };

    fetchImage();

    return () => {
      if (imageUrl) {
        URL.revokeObjectURL(imageUrl);
      }
    };
  }, [creator]);

    function handleCreatorSelectClick(e: FormEvent) {
    e.preventDefault();
    if (creator.personCategory === PersonCategory.DIRECTOR) {
      setDirector(creator);
    } else if (creator.personCategory === PersonCategory.ACTOR) {
      actors.push(creator);
      setActors(actors);
    }
    setCreators([]);
  }

  return (
    <div className="flex items-center p-4 bg-gray-100 rounded border border-gray-300">
      {imageUrl ? (
        <img
          src={imageUrl}
          alt={`${creator.name} ${creator.surname}`}
          className="w-24 h-24 object-cover mr-5 rounded"
        />
      ) : (
        <div className="w-24 h-24 bg-gray-300 mr-5 rounded" />
      )}
      <div className="flex-1">
        <h3 className="text-xl font-bold text-blue-500 mb-2">{`${creator.name} ${creator.surname}`}</h3>
        {/*<p className="text-base text-gray-800">*/}
        {/*  <strong className="font-semibold text-gray-600">Category:</strong>{" "}*/}
        {/*  {creator.personCategory}*/}
        {/*</p>*/}
        <p className="text-base text-gray-800">
          <strong className="font-semibold text-gray-600">Age:</strong>{" "}
          {creator.age}
        </p>
        <p className="text-base text-gray-800">
          <strong className="font-semibold text-gray-600">Height:</strong>{" "}
          {creator.heightCm} cm
        </p>
        <p className="text-base text-gray-800">
          <strong className="font-semibold text-gray-600">Born:</strong>{" "}
          {creator.bornPlace}
        </p>
        <p className="text-base text-gray-800">
          <strong className="font-semibold text-gray-600">Latin Name:</strong>{" "}
          {`${creator.nameLatin} ${creator.surnameLatin}`}
        </p>
        <p className="text-base text-gray-800">
          <strong className="font-semibold text-gray-600">Status:</strong>{" "}
          {creator.isDead ? "Deceased" : "Alive"}
        </p>
        <p className="text-base text-gray-800">
          <strong className="font-semibold text-gray-600">Birth Date:</strong>{" "}
          {creator.birthDate}
        </p>
        {creator.isDead && (
          <p className="text-base text-gray-800">
            <strong className="font-semibold text-gray-600">Death Date:</strong>{" "}
            {creator.deathDate}
          </p>
        )}
        {/* Add the Select button */}
        <button
          onClick={handleCreatorSelectClick}
          className="mt-2 p-2 bg-blue-500 text-white rounded cursor-pointer hover:bg-blue-700"
        >
          Выбрать
        </button>
      </div>
    </div>
  );
};

export default FormFilmCrewChooser;
