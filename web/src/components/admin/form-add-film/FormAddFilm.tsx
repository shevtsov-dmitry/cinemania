import Constants from '@/src/constants/Constants'
import useFormAddFilmStore from '@/src/state/formAddFilmState'
import useFilmCrewChooserStore from '@/src/state/formFilmCrewChooserState'
import ContentCreator from '@/src/types/ContentCreator'
import ContentMetadata from '@/src/types/ContentMetadata'
import Position from '@/src/types/Position'
import React, {
    FormEvent,
    ReactElement,
    useEffect,
    useRef,
    useState,
} from 'react'
import FormFilmCrewChooser from './FormFilmCrewChooser'
import ContentMetadataDTO from '@/src/types/ContentMetadataDTO'

export default function FormAddFilm() {
    const STORAGE_URL = Constants.STORAGE_URL

    const formRef = useRef<HTMLFormElement>(null)
    const formSaveStatusRef = useRef<HTMLParagraphElement>(null)
    const loadingRef = useRef<HTMLImageElement>(null)

    const hideFormAddFilm = useFormAddFilmStore(
        (state) => state.hideFormAddFilm
    )
    const isFormFilmCrewChooserVisible = useFilmCrewChooserStore(
        (state) => state.isVisible
    )
    const showFilmCrewChooser = useFilmCrewChooserStore((state) => state.show)
    const setFormFilmCrewChoosingType = useFilmCrewChooserStore(
        (state) => state.setChoosingType
    )
    const actors: ContentCreator[] = useFilmCrewChooserStore(
        (state) => state.selectedActors
    )
    const director: ContentCreator | undefined =
        useFilmCrewChooserStore((state) => state.selectedDirector) ?? undefined

    const [recentFormErrorMessage, setRecentFormErrorMessage] =
        useState<string>('')

    const [isInfoSignActive, setIsInfoSignActive] = useState<boolean>(false)
    const [isPosterFileSelected, setIsPosterFileSelected] =
        useState<boolean>(false)
    const [isVideoShowFileSelected, setIsVideoShowFileSelected] =
        useState<boolean>(false)
    const [isTrailerFileSelected, setIsTrailerFileSelected] =
        useState<boolean>(false)

    enum FilePickerType {
        VIDEO = 'Осн. видеофайл',
        TRAILER = 'Трейлер',
        EPISODE = 'Эпизод',
        POSTER = 'Постер',
    }

    interface PickedFilesProps {
        video: File | null
        trailer: File | null
        episode: File | null
        poster: File | null
    }

    const [pickedFiles, setPickedFiles] = useState<PickedFilesProps>({
        video: null,
        trailer: null,
        episode: null,
        poster: null,
    })

    enum VideoType {
        STANDALONE = 'standalone',
        TRAILER = 'trailer',
        EPISODE = 'episode',
    }

    async function saveFormData() {
        enum OPERATION_STATUS {
            SUCCESS = 'SUCCESS',
            ERROR = 'ERROR',
        }

        try {
            const videoInfo: ContentMetadata = await saveMetadata()
            await uploadPoster(videoInfo.poster?.id as string)
            await uploadVideo(
                videoInfo.standaloneVideoShow?.id as string,
                VideoType.STANDALONE
            )
            await uploadVideo(
                videoInfo.trailer?.id as string,
                VideoType.TRAILER
            )
            displayStatusMessage(OPERATION_STATUS.SUCCESS)
        } catch (e: any) {
            setRecentFormErrorMessage(e.message)
            displayStatusMessage(OPERATION_STATUS.ERROR, e.message)
            console.error(e)
        }

        loadingRef.current!.style.display = 'none'

        async function uploadPoster(id: string) {
            const posterFile = posterInputRef.current!.files?.[0] as File
            if (posterFile == null) {
                throw new Error('Необходимо выбрать постер для видео.')
            }

            const posterFormData = new FormData()
            posterFormData.append('id', id)
            posterFormData.append('image', posterFile)

            const res = await fetch(`${STORAGE_URL}/api/v0/posters/upload`, {
                method: 'POST',
                body: posterFormData,
            })

            if (res.status !== 201) {
                throw new Error(
                    decodeURI(
                        res.headers.get('Message') ??
                            'Ошибка при загрузке постера.'
                    ).replaceAll('+', ' ')
                )
            }
        }

        async function uploadVideo(id: string, videoType: VideoType) {
            if (!standaloneVideoShowRef.current) {
                throw new Error(
                    `Необходимо выбрать видеофайл. Не выбран ${videoType}.`
                )
            }

            const baseUrl = `${STORAGE_URL}/api/v0/videos`
            let videoFile
            let url = ''

            if (videoType === VideoType.STANDALONE) {
                url = baseUrl + `/standalone`
                videoFile = standaloneVideoShowRef.current.files?.[0] as File
            } else if (videoType === VideoType.TRAILER) {
                url = baseUrl + `/trailer`
                videoFile = standaloneVideoShowRef.current.files?.[0] as File
            } else if (videoType === VideoType.EPISODE) {
                url = baseUrl + `/episode`
                videoFile = standaloneVideoShowRef.current.files?.[0] as File
            } else {
                throw new Error('Не удалось выбрать виодефайл.')
            }

            const videoFormData = new FormData()
            videoFormData.append('id', id)
            videoFormData.append('video', videoFile)

            const res = await fetch(url, {
                method: 'POST',
                body: videoFormData,
            })

            if (res.status !== 201) {
                throw new Error(
                    decodeURI(
                        res.headers.get('Message') ?? 'Ошибка сохранения видео.'
                    ).replaceAll('+', ' ')
                )
            }
        }

        async function saveMetadata(): Promise<ContentMetadata> {
            const form = new FormData(formRef.current!)
            const posterFile = posterInputRef.current!.files?.[0] as File
            const trailerFile = trailerInputRef.current!.files?.[0] as File
            const standaloneVideoShowFile = trailerInputRef.current!
                .files?.[0] as File

            const metadata: ContentMetadataDTO = {
                title: form.get('title')?.toString().trim()!,
                releaseDate: parseDateEngToRus(
                    form.get('releaseDate')?.toString().trim()!
                ),
                countryName: form.get('country')?.toString().trim()!,
                mainGenreName: form.get('mainGenre')?.toString().trim()!,
                description: form.get('description')?.toString().trim()!,
                subGenresNames: parseSubGenres(
                    form.get('subGenres')?.toString()!
                ),
                age: parseInt(form.get('age')?.toString()!),
                rating: parseInt(form.get('rating')?.toString()!),
                filmingGroupDTO: {
                    directorId: director!.id,
                    actorsIds: actors.map((actor) => actor.id),
                },
                poster: {
                    filename: posterFile.name,
                    contentType: posterFile.type,
                    size: posterFile.size,
                },
                trailer: {
                    filename: trailerFile.name,
                    contentType: trailerFile.type,
                    size: trailerFile.size,
                },
                standaloneVideoShow: {
                    filename: standaloneVideoShowFile.name,
                    contentType: standaloneVideoShowFile.type,
                    size: standaloneVideoShowFile.size,
                },
                episodes: [],
            }

            const res = await fetch(`${STORAGE_URL}/api/v0/metadata`, {
                method: 'POST',
                headers: {
                    'Content-type': 'application/json',
                },
                body: JSON.stringify(metadata),
            })

            if (res.status !== 201) {
                throw new Error(
                    decodeURI(
                        res.headers.get('Message') ??
                            'Ошибка сохранения метаданных.'
                    ).replaceAll('+', ' ')
                )
            }

            return res.json()
        }

        function parseDateEngToRus(date: string): string {
            const dateObj = new Date(date)
            const options: Intl.DateTimeFormatOptions = {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
            }
            return dateObj.toLocaleDateString('ru-RU', options)
        }

        function parseSubGenres(subGenresString: string): string[] {
            const splitted = subGenresString.split(',')
            if (splitted.length === 0) {
                return []
            }
            let subGenresArray = []
            for (const string of splitted) {
                if (string.length !== 0) subGenresArray.push(string.trim())
            }
            return subGenresArray
        }

        function displayStatusMessage(
            operationStatus: OPERATION_STATUS,
            errmes?: string
        ) {
            setIsInfoSignActive(false)
            const statusBar = formSaveStatusRef.current!
            statusBar.style.fontSize = '0.8em'
            statusBar.style.marginTop = '-6px'

            switch (operationStatus) {
                case OPERATION_STATUS.SUCCESS: {
                    statusBar.textContent = 'Сохранено ✅'
                    statusBar.style.color = 'green'
                    break
                }
                case OPERATION_STATUS.ERROR: {
                    statusBar.textContent = `${errmes}`
                    statusBar.style.color = 'red'
                    break
                }
                default:
                    break
            }

            setTimeout(() => {
                setIsInfoSignActive(true)
                statusBar.textContent = ''
                statusBar.style.fontSize = '0.7em'
            }, 2000)
        }
    }

    const styles = {
        textInput:
            ' block w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm text-gray-700 placeholder-gray-400 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100 ',
        label: ' mb-1 block text-sm font-medium ',
    }

    const AgeRadioInput = ({ age }: { age: number }) => (
        <div>
            <input
                onKeyDown={(event) =>
                    event.keyCode === 13 && event.preventDefault()
                }
                type="radio"
                name="age"
                value={age}
            />
            <label htmlFor="" className="ml-0.5">
                {age}+
            </label>
        </div>
    )

    function handleSelect() {
        setFormFilmCrewChoosingType(category)
        showFilmCrewChooser()
    }

    interface PersonPositionOptionProps {
        label: string
        category: Position
    }

    const PersonPositionOption = ({
        label,
        category,
    }: PersonPositionOptionProps) => (
        <div className="flex w-full flex-col items-center justify-center">
            <label className={styles.label + ' text-center'}>{label}</label>
            <button
                className={`block w-10/12 rounded-md border-0 bg-blue-50 px-3 py-2 text-sm text-blue-700 dark:bg-neutral-700 dark:text-white`}
                onClick={(e) => {
                    e.preventDefault()
                }}
            >
                Выбрать
            </button>
        </div>
    )

    function handleSubmitButton(e: FormEvent) {
        e.preventDefault()
        showLoadingIcon()
        saveFormData()

        function showLoadingIcon() {
            loadingRef.current!.style.display = 'block'
        }
    }

    interface FilePickerProps {
        type: FilePickerType
    }

    const FilePicker = ({ type }: FilePickerProps) => {
        const [isSelected, setIsSelected] = useState<boolean>(false)
        const selfInputRef = useRef<HTMLInputElement>(null)

        function handleSelectedFiles(e: React.ChangeEvent<HTMLInputElement>) {
            const file = e.target.files?.[0] || null
            if (file) {
                setIsSelected(true)
            }

            setPickedFiles((prev) => ({
                ...prev,
                [type]: file,
            }))
        }

        return (
            <div className="space-y-1">
                <label className={styles.label}>{type.toString()}</label>
                <input
                    ref={selfInputRef}
                    type="file"
                    onChange={handleSelectedFiles}
                    className={
                        isSelected
                            ? 'file:bg-green-500'
                            : 'file:bg-blue-50' +
                              ` block w-full text-sm text-gray-700 file:mr-4 file:rounded-md file:border-0 file:px-3 file:py-2 file:text-blue-700 hover:file:bg-blue-100 dark:text-blue-100 dark:file:bg-neutral-600 dark:file:text-blue-100`
                    }
                />
            </div>
        )
    }

    return (
        <div className="flex min-h-full w-full items-center justify-center bg-gray-100 dark:bg-gray-900">
            <form
                ref={formRef}
                className="relative w-full max-w-md rounded-lg bg-white p-6 shadow-lg dark:bg-neutral-800 dark:text-blue-100"
            >
                <div className="absolute right-4 top-4">
                    <button
                        id="close-sign"
                        className="cursor-pointer select-none text-xl font-bold text-gray-500 hover:text-gray-700 dark:text-blue-200 dark:hover:text-blue-300"
                        onClick={hideFormAddFilm}
                    >
                        &#10006;
                    </button>
                </div>

                <h2 className="mb-6 text-center text-3xl font-bold text-gray-900 dark:text-blue-100">
                    Добавить фильм
                </h2>

                <ul className="space-y-4">
                    <li id="title">
                        <label className={styles.label}>Название фильма</label>
                        <input
                            onKeyDown={(event) =>
                                event.keyCode === 13 && event.preventDefault()
                            }
                            onSubmit={(event) => event.preventDefault()}
                            className="block w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm text-gray-700 placeholder-gray-400 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
                            type="search"
                            name={'title'}
                        />
                    </li>

                    <li className="flex gap-3">
                        <div id="country">
                            <label className={styles.label}>Страна</label>
                            <input
                                onKeyDown={(event) =>
                                    event.keyCode === 13 &&
                                    event.preventDefault()
                                }
                                className="block w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm text-gray-700 placeholder-gray-400 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
                                type="search"
                                name="country"
                            />
                        </div>

                        <div id="releaseDate">
                            <label className={styles.label}>Дата релиза</label>
                            <input
                                onKeyDown={(event) =>
                                    event.keyCode === 13 &&
                                    event.preventDefault()
                                }
                                className="block w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm text-gray-700 placeholder-gray-400 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
                                type="date"
                                name="releaseDate"
                            />
                        </div>
                    </li>

                    <li className="flex w-full gap-3">
                        <div id="mainGenre" className="">
                            <label className={styles.label}>
                                Основной жанр
                            </label>
                            <input
                                onKeyDown={(event) =>
                                    event.keyCode === 13 &&
                                    event.preventDefault()
                                }
                                className="focus:outdivne-none block w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm text-gray-700 placeholder-gray-400 shadow-sm focus:border-blue-500 focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
                                type="search"
                                name="mainGenre"
                            />
                        </div>

                        <div id="subGenres" className="w-full">
                            <label className={styles.label}>Поджанры</label>
                            <input
                                onKeyDown={(event) =>
                                    event.keyCode === 13 &&
                                    event.preventDefault()
                                }
                                className="focus:outdivne-none block w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm text-gray-700 placeholder-gray-400 shadow-sm focus:border-blue-500 focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
                                type="search"
                                name="subGenres"
                                placeholder="разделять запятой"
                                onChange={() => {}}
                            />
                        </div>
                    </li>

                    <li id="description">
                        <label className={styles.label}>Описание</label>
                        <textarea
                            name="description"
                            className="focus:outdivne-none block w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm text-gray-700 placeholder-gray-400 shadow-sm focus:border-blue-500 focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
                            rows={4}
                            cols={50}
                            placeholder="Описание фильма"
                            onChange={() => {}}
                        ></textarea>
                    </li>

                    <li className="flex w-full gap-3">
                        <div className="w-fit flex-1">
                            <label className={styles.label}>Рейтинг</label>
                            <input
                                onKeyDown={(event) =>
                                    event.keyCode === 13 &&
                                    event.preventDefault()
                                }
                                className="focus:outdivne-none block w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm text-gray-700 placeholder-gray-400 shadow-sm focus:border-blue-500 focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
                                type="number"
                                placeholder="6.89"
                                name="rating"
                            />
                        </div>

                        <div id="ageRestriction" className="w-full flex-[4]">
                            <p className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                                Возраст
                            </p>
                            <div className="mt-2.5 flex w-full items-center justify-between gap-2">
                                <AgeRadioInput age={0} />
                                <AgeRadioInput age={6} />
                                <AgeRadioInput age={12} />
                                <AgeRadioInput age={16} />
                                <AgeRadioInput age={18} />
                                <AgeRadioInput age={21} />
                            </div>
                        </div>
                    </li>

                    <li id="filmingGroup">
                        <label className={styles.label}>
                            Вkберите съёмочную группу
                        </label>
                        <div className="grid grid-cols-2 gap-1">
                            <PersonPositionOption
                                label="Режиссёр"
                                category={Position.DIRECTOR}
                            />
                            <PersonPositionOption
                                label="Продюсеры"
                                category={Position.PRODUCER}
                            />
                            <PersonPositionOption
                                label="Актёры"
                                category={Position.ACTOR}
                            />
                            <PersonPositionOption
                                label="Операторы"
                                category={Position.OPERATOR}
                            />
                        </div>
                    </li>

                    <li>
                        {isFormFilmCrewChooserVisible && (
                            <div className="fixed bottom-[20%] z-10">
                                <FormFilmCrewChooser />
                            </div>
                        )}
                    </li>

                    <li className="flex gap-2">
                        <FilePicker type={FilePickerType.POSTER} />
                        <FilePicker type={FilePickerType.TRAILER} />
                        <FilePicker type={FilePickerType.VIDEO} />
                    </li>
                </ul>

                <div className="relative mt-6 flex items-center justify-center">
                    {/* Status & Loading */}
                    <div className="absolute left-0 flex h-12 w-24 items-center justify-center overflow-hidden">
                        <p
                            className="m-0 p-0 text-center text-xs font-medium text-gray-700 dark:text-blue-100"
                            ref={formSaveStatusRef}
                        />
                        {isInfoSignActive && (
                            <img
                                src="assets/images/icons/info-sign.svg"
                                onClick={() => alert(recentFormErrorMessage)}
                            />
                        )}
                        <img
                            ref={loadingRef}
                            src="assets/images/icons/loading.gif"
                            className="hidden w-7"
                        />
                    </div>
                    {/* Submit Button */}
                    <button
                        onKeyDown={(event) =>
                            event.keyCode === 13 && event.preventDefault()
                        }
                        className="rounded-lg bg-blue-600 px-4 py-2 font-bold text-white transition-transform hover:bg-blue-700 focus:outline-none dark:bg-green-600 dark:hover:bg-green-700"
                        id="add-film-button"
                        onClick={handleSubmitButton}
                    >
                        Загрузить
                    </button>
                    {/* Clear Form */}
                    <div className="absolute right-0">
                        <button className="m-0 p-0 text-xs text-gray-500 hover:text-gray-700 dark:text-blue-200 dark:hover:text-blue-300">
                            <u>Очистить форму</u>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    )
}
