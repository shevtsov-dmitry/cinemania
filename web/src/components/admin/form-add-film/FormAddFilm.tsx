import PersonCategory from '@/src/types/PersonCategory'
import React, { FormEvent, useEffect, useRef, useState } from 'react'
import Constants from '@/src/constants/Constants'
import useFormAddFilmStore from '@/src/state/formAddFilmState'
import FormFilmCrewChooser from './FormFilmCrewChooser'
import useFilmCrewChooserStore from '@/src/state/formFilmCrewChooserState'
import formAddCreator from '@/src/components/admin/form-add-creator/FormAddCreator'
import ContentCreator from '@/src/types/ContentCreator'
import ContentMetadata from '@/src/types/ContentMetadata'
import FilmingGroup from '@/src/types/FilmingGroup'

export default function FormAddFilm() {
    const STORAGE_URL = Constants.STORAGE_URL

    const formRef = useRef<HTMLFormElement>(null)
    const posterInputRef = useRef<HTMLInputElement>(null)
    const trailerInputRef = useRef<HTMLInputElement>(null)
    const standaloneVideoShowRef = useRef<HTMLInputElement>(null)
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

    async function saveFormData() {
        enum OPERATION_STATUS {
            SUCCESS = 'SUCCESS',
            ERROR = 'ERROR',
        }

        try {
            const videoInfo: ContentMetadata = await saveMetadata()
            await uploadPoster(videoInfo.poster?.id as string)
            await uploadVideo(videoInfo.standaloneVideoShow?.id as string)
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
            posterFormData.append('image', posterFile)
            posterFormData.append('id', id)

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
            return OPERATION_STATUS.SUCCESS
        }

        async function uploadVideo(id: string) {
            if (!standaloneVideoShowRef.current) {
                throw new Error('Необходимо выбрать видеофайл.')
            }

            const videoFile = standaloneVideoShowRef.current.files?.[0] as File

            const videoFormData = new FormData()
            videoFormData.append('id', id)
            videoFormData.append('video', videoFile)

            const res = await fetch(`${STORAGE_URL}/api/v0/videos/standalone`, {
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

            const metadata: ContentMetadata = {
                title: form.get('rating')?.toString().trim()!,
                releaseDate: parseDateEngToRus(
                    form.get('releaseDate')?.toString().trim()!
                ),
                country: form.get('country')?.toString().trim()!,
                mainGenre: form.get('mainGenre')?.toString().trim()!,
                description: form.get('description')?.toString().trim()!,
                subGenres: parseSubGenres(form.get('subGenres')?.toString()!),
                age: parseInt(form.get('age')?.toString()!),
                rating: parseInt(form.get('rating')?.toString()!),
                filmingGroup: {
                    director: director!,
                    actors,
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
                // tvSeries: undefined,
            }

            const res = await fetch(`${STORAGE_URL}/api/v0/metadata`, {
                method: 'POST',
                headers: {
                    'Content-type': 'application/json',
                },
                body: JSON.stringify(metadata!),
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
        }

        // function validateFormInputs(form: FormData) {
        //     if (!form.get('title') || isBlank(form.get('title')))
        //         throw new Error('Необходимо указать название')
        //     else if (!form.get('country') || isBlank(form.get('country')))
        //         throw new Error('Необходимо указать страну')
        //     else if (!form.get('releaseDate'))
        //         throw new Error('Необходимо указать дату релиза')
        //     else if (!form.get('mainGenre') || isBlank(form.get('mainGenre')))
        //         throw new Error('Необходимо указать основной жанр')
        //     else if (!form.get('age'))
        //         throw new Error('Необходимо указать возрастное ограничение')
        //     else if (!form.get('rating') || isBlank(form.get('rating')))
        //         throw new Error('Необходимо указать рейтинг')
        //     else if (
        //         isNaN(form.get('rating')) ||
        //         Number(form.get('rating')) < 0 ||
        //         Number(form.get('rating')) > 10
        //     ) {
        //         throw new Error('Рейтинг должен быть числом от 0 до 10')
        //     }

        //     // Validate Poster File
        //     const posterFiles = posterInputRef.current.files
        //     if (!posterFiles || posterFiles.length === 0) {
        //         throw new Error('Необходимо загрузить постер')
        //     } else {
        //         const posterFile = posterFiles[0]
        //         const allowedPosterTypes = [
        //             'image/jpeg',
        //             'image/png',
        //             'image/gif',
        //         ]
        //         if (!allowedPosterTypes.includes(posterFile.type))
        //             throw new Error('Неподдерживаемый тип файла для постера')
        //         const maxPosterSize = 5 * 1024 * 1024 // 5MB
        //         if (posterFile.size > maxPosterSize)
        //             throw new Error(
        //                 'Размер файла постера не должен превышать 5MB'
        //             )
        //     }

        //     // Validate Video File
        //     const videoFiles = standaloneVideoShowRef.current.files
        //     if (!videoFiles || videoFiles.length === 0) {
        //         throw new Error('Необходимо загрузить видео')
        //     } else {
        //         const videoFile = videoFiles[0]
        //         // Optional: Validate file type and size
        //         const allowedVideoTypes = [
        //             'video/mp4',
        //             'video/avi',
        //             'video/mov',
        //         ]
        //         if (!allowedVideoTypes.includes(videoFile.type)) {
        //             throw new Error('Неподдерживаемый тип файла для видео')
        //         }
        //         const maxVideoSize = 20 * 1024 * 1024 * 1024
        //         if (videoFile.size > maxVideoSize) {
        //             throw new Error(
        //                 'Размер файла видео не должен превышать 20GB'
        //             )
        //         }
        //     }
        // }

        function isBlank(str: string): boolean {
            return !str || str.trim() === ''
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

    function handleSubmitButton(e: FormEvent) {
        e.preventDefault()
        animateButtonPress()
        showLoadingIcon()
        saveFormData()

        function animateButtonPress() {
            const el = e.currentTarget
            el.style.transform = 'scale(0.95)'
            el.classList.add('bg-green-600')
            setTimeout(() => {
                el.style.transform = 'scale(1)'
                el.classList.remove('bg-green-600')
            }, 230)
        }

        function showLoadingIcon() {
            loadingRef.current!.style.display = 'block'
        }
    }

    return (
        <div className="flex min-h-screen w-full items-center justify-center bg-gray-100 p-4 dark:bg-gray-900">
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
                        <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                            Название фильма
                        </label>
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
                            <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                                Страна
                            </label>
                            <input
                                onKeyDown={(event) =>
                                    event.keyCode === 13 &&
                                    event.preventDefault()
                                }
                                className="block w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm text-gray-700 placeholder-gray-400 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100"
                                type="search"
                                name="country"
                            />
                            {/* <div className="relative mt-1"> */}
                            {/*   <div className="absolute z-10 w-full bg-white shadow-lg dark:bg-neutral-700"> */}
                            {/*     {countrySuggestionsDOM} */}
                            {/*   </div> */}
                            {/* </div> */}
                        </div>

                        <div id="releaseDate">
                            <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                                Дата релиза
                            </label>
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
                            <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
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
                                // TODO restore autosuggesstion in the future
                                // value={mainGenreInput}
                                // onChange={(ev) => setMainGenreInput(ev.target.value)}
                            />
                            {/* <div className="relative mt-1"> */}
                            {/*   <div className="absolute z-10 w-full bg-white shadow-lg dark:bg-neutral-700"> */}
                            {/*     {mainGenreSuggestionsDOM} */}
                            {/*   </div> */}
                            {/* </div> */}
                        </div>

                        <div id="subGenres" className="w-full">
                            <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                                Поджанры
                            </label>
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
                            {/*   <div className="relative mt-1"> */}
                            {/*     <div className="absolute z-10 w-full bg-white shadow-lg dark:bg-neutral-700"> */}
                            {/*       {subGenresSuggestionsDOM} */}
                            {/*     </div> */}
                            {/*   </div> */}
                        </div>
                    </li>

                    <li id="description">
                        <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                            Описание
                        </label>
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
                            <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                                Рейтинг
                            </label>
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
                            </div>
                        </div>
                    </li>

                    <li id="filmingGroup">
                        <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                            Выберите съёмочную группу
                        </label>
                        <div className="flex gap-2">
                            <div>
                                <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                                    Режиссёр
                                </label>
                                <button
                                    className={`mr-4 block w-full rounded-md border-0 px-3 py-2 text-sm text-blue-700 dark:bg-neutral-700 dark:text-white ${director ? 'bg-green-500 text-white' : 'bg-blue-50'}`}
                                    onClick={(e) => {
                                        e.preventDefault()
                                        setFormFilmCrewChoosingType(
                                            PersonCategory.DIRECTOR
                                        )
                                        showFilmCrewChooser()
                                    }}
                                >
                                    Выбрать
                                </button>
                            </div>

                            <div>
                                <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                                    Актёры
                                </label>
                                <button
                                    className={`mr-4 block w-full rounded-md border-0 px-3 py-2 text-sm text-blue-700 dark:bg-neutral-700 dark:text-white ${actors.length > 0 ? 'bg-green-500 text-white' : 'bg-blue-50'}`}
                                    onClick={(e) => {
                                        e.preventDefault()
                                        setFormFilmCrewChoosingType(
                                            PersonCategory.ACTOR
                                        )
                                        showFilmCrewChooser()
                                    }}
                                >
                                    Выбрать
                                </button>
                            </div>
                        </div>
                    </li>

                    <li>
                        {isFormFilmCrewChooserVisible && (
                            <div className="fixed bottom-[20%] z-10">
                                <FormFilmCrewChooser />
                            </div>
                        )}
                    </li>

                    <li className="flex gap-3">
                        <div id="poster">
                            <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                                Постер
                            </label>
                            <input
                                ref={posterInputRef}
                                type="file"
                                onChange={() => setIsPosterFileSelected(true)}
                                className={`block w-full text-sm text-gray-700 file:mr-4 file:rounded-md file:border-0 file:px-3 file:py-2 file:text-blue-700 hover:file:bg-blue-100 dark:text-blue-100 dark:file:bg-neutral-600 dark:file:text-blue-100 ${
                                    isPosterFileSelected
                                        ? 'file:bg-green-500 file:text-white'
                                        : 'file:bg-blue-50'
                                }`}
                                name="imageUrl"
                            />
                        </div>

                        <div id="trailer">
                            <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                                Трейлер
                            </label>
                            <input
                                ref={trailerInputRef}
                                type="file"
                                onChange={() => setIsTrailerFileSelected(true)}
                                className={`block w-full text-sm text-gray-700 file:mr-4 file:rounded-md file:border-0 file:px-3 file:py-2 file:text-blue-700 hover:file:bg-blue-100 dark:text-blue-100 dark:file:bg-neutral-600 dark:file:text-blue-100 ${
                                    isTrailerFileSelected
                                        ? 'file:bg-green-500 file:text-white'
                                        : 'file:bg-blue-50'
                                }`}
                                name="trailerUrl"
                            />
                        </div>

                        <div id="video">
                            <label className="mb-1 block text-sm font-medium text-gray-700 dark:text-blue-100">
                                Видео
                            </label>
                            <input
                                ref={standaloneVideoShowRef}
                                type="file"
                                onChange={() =>
                                    setIsVideoShowFileSelected(true)
                                }
                                className={`block w-full text-sm text-gray-700 file:mr-4 file:rounded-md file:border-0 file:px-3 file:py-2 file:text-blue-700 hover:file:bg-blue-100 dark:text-blue-100 dark:file:bg-neutral-600 dark:file:text-blue-100 ${
                                    isVideoShowFileSelected
                                        ? 'file:bg-green-500 file:text-white'
                                        : 'file:bg-blue-50'
                                }`}
                                name="videoUrl"
                            />
                        </div>
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
                                // TODO better use popup than alert
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
