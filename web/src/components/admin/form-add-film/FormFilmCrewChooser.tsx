import Constants from '@/src/constants/Constants'
import useFilmCrewChooserStore from '@/src/state/formFilmCrewChooserState'
import ContentCreator from '@/src/types/ContentCreator'
import Position from '@/src/types/Position'
import React, {
    Dispatch,
    FormEvent,
    ReactElement,
    SetStateAction,
    useEffect,
    useState,
} from 'react'

const FormFilmCrewChooser = (): ReactElement => {
    const [searchTerm, setSearchTerm] = useState<string>('')
    const [creators, setCreators] = useState<ContentCreator[]>([])
    const [errorMessage, setErrorMessage] = useState<string | null>(null)
    const [creatorTypeHeaderLabel, setCreatorTypeHeaderLabel] =
        useState<string>()

    const hideFilmCrewChooser = useFilmCrewChooserStore((state) => state.hide)

    const creatorType: Position = useFilmCrewChooserStore(
        (state) => state.choosingType
    )
    const selectedActorsList: ContentCreator[] = useFilmCrewChooserStore(
        (state) => state.selectedActors
    )

    const selectedDirector = useFilmCrewChooserStore(
        (state) => state.selectedDirector
    ) as ContentCreator

    useEffect(() => {
        if (creatorType === Position.DIRECTOR) {
            setCreatorTypeHeaderLabel('Выбор режиссёра')
        } else if (creatorType === Position.ACTOR) {
            setCreatorTypeHeaderLabel('Выбор актёров')
        }
    }, [creatorType])

    const handleSearch = async (e: FormEvent) => {
        e.preventDefault()
        setErrorMessage(null)
        if (!searchTerm.trim()) {
            setErrorMessage('Введите имя или фамилию для поиска.')
            return
        }

        let response
        try {
            response = await fetch(
                Constants.STORAGE_URL +
                    `/api/v0/metadata/content-creators/fullname/${encodeURIComponent(
                        searchTerm
                    )}`
            )

            if (!response.ok) {
                throw new Error()
            }

            const data: ContentCreator[] = await response.json()
            setCreators(data)
        } catch (error) {
            if (response) {
                const errmes: string = decodeURI(
                    response.headers.get('Message') || 'Ошибка при поиске'
                ).replaceAll('+', ' ')
                setErrorMessage(errmes)
            } else {
                setErrorMessage('Ошибка при поиске')
            }
        }
    }

    return (
        <div className="related fixed left-[650px] top-[600px] mx-auto max-w-2xl rounded bg-white p-5 text-gray-800 shadow">
            <div
                id="close-sign"
                className="absolute right-2 top-1 hover:cursor-pointer"
                onClick={hideFilmCrewChooser}
            >
                ✖
            </div>
            <h1 className="font-bold">{creatorTypeHeaderLabel}</h1>
            <div className="mb-5 mt-1 flex gap-2">
                <input
                    id="search-input"
                    type="text"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    placeholder="Поиск по имени или фамилии"
                    className="w-72 rounded border-2 border-blue-500 p-2 text-base"
                />
                <button
                    onClick={handleSearch}
                    className="cursor-pointer rounded bg-blue-500 p-2 text-white hover:bg-blue-700"
                >
                    Искать
                </button>
            </div>
            {errorMessage && (
                <div className="mb-5 text-red-500">{errorMessage}</div>
            )}
            {selectedDirector && creatorType === Position.DIRECTOR && (
                <h1>
                    Выбранный режиссёр: {selectedDirector.name}{' '}
                    {selectedDirector.surname}
                </h1>
            )}
            {creatorType === Position.ACTOR &&
                selectedActorsList.length > 0 && (
                    <div className={'flex gap-2'}>
                        <h1>Выбранные актёры:</h1>
                        <div className={'flex'}>
                            {selectedActorsList.map((actor, idx) => (
                                <div
                                    key={actor.id}
                                    className="flex flex-wrap items-center gap-2 text-sm text-neutral-500"
                                >
                                    {actor.name} {actor.surname}{' '}
                                    {idx !== selectedActorsList.length - 1 &&
                                        ','}
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            <div className="grid grid-cols-2 gap-5">
                {creators.length > 0 &&
                    creators
                        .filter((creator) => creator.position === creatorType)
                        .map((creator) => (
                            <CreatorItem
                                key={creator.id}
                                creator={creator}
                                setCreators={setCreators}
                            />
                        ))}
            </div>
        </div>
    )
}

interface CreatorItemProps {
    creator: ContentCreator
    setCreators: Dispatch<SetStateAction<ContentCreator[]>>
}

const CreatorItem = ({
    creator,
    setCreators,
}: CreatorItemProps): ReactElement => {
    const [imageUrl, setImageUrl] = useState<string | null>(null)
    const setDirector = useFilmCrewChooserStore(
        (state) => state.setSelectedDirector
    )
    const setActors = useFilmCrewChooserStore(
        (state) => state.setSelectedActors
    )
    const actors: ContentCreator[] = useFilmCrewChooserStore(
        (state) => state.selectedActors
    )

    useEffect(() => {
        const fetchImage = async () => {
            try {
                const response = await fetch(
                    Constants.STORAGE_URL +
                        `/api/v0/metadata/content-creators/user-pics/${creator.position}/${creator.userPic.id}`
                )
                if (!response.ok) {
                    throw new Error(
                        `Ошибка ${response.status} при загрузке изображения.`
                    )
                }
                const blob = await response.blob()
                const url = URL.createObjectURL(blob)
                setImageUrl(url)
            } catch (error) {
                console.error('Error fetching image:', error)
            }
        }

        fetchImage()

        return () => {
            if (imageUrl) {
                URL.revokeObjectURL(imageUrl)
            }
        }
    }, [creator])

    function handleCreatorSelectClick(e: FormEvent) {
        e.preventDefault()
        if (creator.position === Position.DIRECTOR) {
            setDirector(creator)
        } else if (creator.position === Position.ACTOR) {
            actors.push(creator)
            setActors(actors)
        }
        setCreators([])
    }

    return (
        <div className="flex items-center gap-2 rounded border border-gray-300 bg-gray-100 p-4">
            <div className="flex-1 items-start justify-start">
                <h3 className="mb-2 text-xl font-bold text-blue-500">{`${creator.name} ${creator.surname}`}</h3>
                {imageUrl ? (
                    <img
                        src={imageUrl}
                        alt={`${creator.name} ${creator.surname}`}
                        className="mr-5 h-24 w-24 rounded object-cover"
                    />
                ) : (
                    <div className="mr-5 h-24 w-24 rounded bg-gray-300" />
                )}
            </div>
            <div className="flex-2">
                {/*<p className="text-base text-gray-800">*/}
                {/*  <strong className="font-semibold text-gray-600">Category:</strong>{" "}*/}
                {/*  {creator.personCategory}*/}
                {/*</p>*/}
                <p className="text-base text-gray-800">
                    <strong className="font-semibold text-gray-600">
                        Дата рождения:
                    </strong>{' '}
                    {creator.birthDate}
                </p>
                {creator.isDead && (
                    <p className="text-base text-gray-800">
                        <strong className="font-semibold text-gray-600">
                            Дата смерти:
                        </strong>{' '}
                        {creator.deathDate}
                    </p>
                )}
                <p className="text-base text-gray-800">
                    <strong className="font-semibold text-gray-600">
                        Рост:
                    </strong>{' '}
                    {creator.heightCm} см
                </p>
                <p className="text-base text-gray-800">
                    <strong className="font-semibold text-gray-600">
                        Рождён(а):
                    </strong>{' '}
                    {creator.bornPlace}
                </p>
                {/* Add the Select button */}
                <button
                    onClick={handleCreatorSelectClick}
                    className="mt-2 cursor-pointer rounded bg-blue-500 p-2 text-white hover:bg-blue-700"
                >
                    Выбрать
                </button>
            </div>
        </div>
    )
}

export default FormFilmCrewChooser
