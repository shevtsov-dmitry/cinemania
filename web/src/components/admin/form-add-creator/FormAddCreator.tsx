import Constants from '@/src/constants/Constants'
import useFormAddCreatorStore from '@/src/state/formAddCreatorState'
import ContentMetadata from '@/src/types/ContentMetadata'
import Position, {
    PositionKindLocalized,
} from '@/src/types/Position'
import UserPic from '@/src/types/UserPic'
import React, {
    FormEvent,
    ReactElement,
    useEffect,
    useRef,
    useState,
} from 'react'

const FormAddCreator = () => {
    const [name, setName] = useState<string>('')
    const [nameLatin, setNameLatin] = useState<string>('')
    const [surname, setSurname] = useState<string | null>('')
    const [surnameLatin, setSurnameLatin] = useState<string | null>('')
    const [bornPlace, setBornPlace] = useState<string | null>('')
    const [heightMeters, setHeightMeters] = useState<number | null>(0)
    const [age, setAge] = useState<number>(0)
    const [birthDate, setBirthDate] = useState<string>('')
    const [deathDate, setDeathDate] = useState<string | null>('')
    const [personCategory, setPersonCategory] = useState<Position>()
    const [filmsParticipated, setFilmsParticipated] = useState<
        ContentMetadata[]
    >([])
    const [isDead, setIsDead] = useState<boolean>(false)

    interface OperationStatus {
        ok: boolean
        message?: string
    }

    const [operationStatus, setOperationStatus] = useState<
        OperationStatus | undefined
    >()

    const [selectedPersonAvatarImage, setSelectedPersonAvatarImage] = useState<
        File | undefined
    >()

    const formRef = useRef<HTMLFormElement>(null)

    const hideFormAddCreator = useFormAddCreatorStore(
        (state) => state.hideFormAddCreator
    )

    async function handleSubmit(e: FormEvent) {
        e.preventDefault()

        const userPicMetadata = await uploadImage()

        const newCreator = {
            name,
            surname,
            nameLatin,
            surnameLatin,
            bornPlace,
            heightMeters,
            age,
            personCategory,
            userPic: userPicMetadata as UserPic,
            birthDate: parseDateEngToRus(birthDate),
            deathDate: isDead ? parseDateEngToRus(deathDate!) : null,
            isDead,
        }

        const res = await fetch(
            Constants.STORAGE_URL + '/api/v0/metadata/content-creators',
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(newCreator),
            }
        )

        if (!res.ok) {
            const errmes = decodeURI(
                res.headers.get('Message') as string
            ).replaceAll('+', ' ')
            setOperationStatus({ ok: false, message: errmes })
            console.error(errmes)
        } else {
            setOperationStatus({ ok: true, message: '✓' })
        }
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

    async function uploadImage(): Promise<UserPic | undefined> {
        if (selectedPersonAvatarImage) {
            const formData = new FormData()
            formData.append('image', selectedPersonAvatarImage)
            formData.append('personCategory', personCategory as string)

            const res = await fetch(
                Constants.STORAGE_URL +
                    '/api/v0/metadata/content-creators/user-pics/upload',
                {
                    method: 'POST',
                    body: formData,
                }
            )

            if (!res.ok) {
                const errmes: string = decodeURI(
                    res.headers.get('Message') as string
                ).replaceAll('+', ' ')
                setOperationStatus({ ok: false, message: errmes })
                console.error(errmes)
                return
            }

            return await res.json()
        }
    }

    function resetForm() {
        setName('')
        setNameLatin('')
        setSurname('')
        setSurnameLatin('')
        setBornPlace('')
        setHeightMeters(0)
        setAge(0)
        setBirthDate('')
        setDeathDate('')
        setIsDead(false)
        setFilmsParticipated([])
        if (formRef.current) formRef.current.reset()
    }

    useEffect(() => {
        if (!operationStatus) return

        const timer = setTimeout(() => {
            setOperationStatus(undefined)
        }, 3000)
        return () => clearTimeout(timer)
    }, [operationStatus])

    interface LocalizedPersonCategoryList {
        locale: Intl.Locale
        optionNo?: boolean
    }
    const LocalizedPersonCategoryList = ({
        locale,
        optionNo,
    }: LocalizedPersonCategoryList): ReactElement => {
        const localizedNames: string[] = []

        if (optionNo) {
            localizedNames.push('Нет')
        }

        if (locale.baseName === 'ru') {
            for (let category of Object.values(Position)) {
                localizedNames.push(PositionKindLocalized.RU[category])
            }
        }

        return localizedNames.map((category) => (
            <option key={category} value={category}>
                {category}
            </option>
        ))
    }

    const styles = {
        textInput:
            'block w-full rounded-md border border-gray-300 bg-white px-3 py-2 text-sm text-gray-700 placeholder-gray-400 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 dark:border-neutral-600 dark:bg-neutral-700 dark:text-blue-100',
        label: ' mb-1 block text-sm font-medium ',
    }

    return (
        <div className="flex min-h-full w-full items-center justify-center bg-gray-100 dark:bg-gray-900">
            <form
                ref={formRef}
                className="full relative max-w-md rounded-lg bg-white p-6 shadow-lg dark:bg-neutral-800 dark:text-blue-100"
                onSubmit={handleSubmit}
            >
                <div id="close-sign" className="absolute right-4 top-4">
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
                            <label className={styles.label}>Имя</label>
                            <input
                                className={styles.textInput}
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                placeholder="Введите имя"
                            />
                        </div>
                        <div>
                            <label className={styles.label}>Фамилия</label>
                            <input
                                className={styles.textInput}
                                value={surname!}
                                onChange={(e) => setSurname(e.target.value)}
                                placeholder="Введите фамилию"
                            />
                        </div>
                    </li>

                    <li className="flex gap-3">
                        <div>
                            <label className={styles.label}>
                                Имя (Латиницей)
                            </label>
                            <input
                                className={styles.textInput}
                                value={nameLatin}
                                onChange={(e) => setNameLatin(e.target.value)}
                                placeholder="Введите имя латиницей"
                            />
                        </div>
                        <div>
                            <label className={styles.label}>
                                Фамилия (Латиницей)
                            </label>
                            <input
                                className={styles.textInput}
                                value={surnameLatin!}
                                onChange={(e) =>
                                    setSurnameLatin(e.target.value)
                                }
                                placeholder="Введите Фамилию латиницей"
                            />
                        </div>
                    </li>

                    <li>
                        <label className={styles.label}>Место рождения</label>
                        <input
                            className={styles.textInput}
                            value={bornPlace!}
                            onChange={(e) => setBornPlace(e.target.value)}
                            placeholder="Введите страну и город; прим. Беларусь, Минск"
                        />
                    </li>

                    <li className="flex gap-4">
                        <div className="flex-2">
                            <label className={styles.label}>Должность</label>
                            <div className="flex gap-1">
                                <select
                                    className="mt-0.5 rounded p-1"
                                    value={personCategory}
                                    onChange={(e) =>
                                        setPersonCategory(
                                            e.target.value as Position
                                        )
                                    }
                                >
                                    <LocalizedPersonCategoryList
                                        locale={new Intl.Locale('RU')}
                                    />
                                </select>
                            </div>
                        </div>

                        <div className="flex-2 mr-12">
                            <label className={styles.label}>
                                Доп. Должность
                            </label>
                            <div className="flex gap-1">
                                <select
                                    className={'mt-0.5 rounded p-1'}
                                    value={personCategory}
                                    onChange={(e) =>
                                        setPersonCategory(
                                            e.target.value as Position
                                        )
                                    }
                                >
                                    <LocalizedPersonCategoryList
                                        locale={new Intl.Locale('RU')}
                                        optionNo={true}
                                    />
                                </select>
                            </div>
                        </div>

                        <div className="flex-1">
                            <label className={styles.label}>Рост (см)</label>
                            <input
                                className={styles.textInput}
                                type="number"
                                value={heightMeters!}
                                onChange={(e) =>
                                    setHeightMeters(
                                        parseFloat(e.target.value) || 0
                                    )
                                }
                                placeholder="1.82"
                            />
                        </div>
                    </li>

                    <li className="flex items-center gap-2">
                        <div className="mb-[-20px] flex gap-1">
                            <label className={styles.label}>{'Мёртв'}</label>
                            <input
                                className="mt-[-2px]"
                                type="checkbox"
                                checked={isDead}
                                onChange={(e) => setIsDead(e.target.checked)}
                            />
                        </div>

                        <div>
                            <label className={styles.label}>
                                Дата рождения
                            </label>
                            <input
                                className={styles.textInput}
                                type="date"
                                value={birthDate}
                                onChange={(e) => setBirthDate(e.target.value)}
                            />
                        </div>

                        {isDead && (
                            <div>
                                <label className={styles.label}>
                                    Дата смерти
                                </label>
                                <input
                                    className={styles.textInput}
                                    type="date"
                                    value={deathDate}
                                    onChange={(e) =>
                                        setDeathDate(e.target.value)
                                    }
                                />
                            </div>
                        )}
                    </li>

                    <li className="flex">
                        <div className="space-y-4">
                            <div>
                                <label className={styles.label}>Фото</label>
                                <input
                                    type="file"
                                    onChange={(e) => {
                                        setSelectedPersonAvatarImage(
                                            e.currentTarget.files?.[0]
                                        )
                                    }}
                                    className={
                                        'block w-full text-sm text-gray-700 file:mr-4 file:rounded-md file:border-0 file:bg-blue-50 file:px-3 file:py-2 file:text-blue-700 hover:file:bg-blue-100 dark:text-blue-100 dark:file:bg-neutral-600 dark:file:text-blue-100'
                                    }
                                    accept="image/*"
                                />
                            </div>
                            <button
                                className={`mr-4 block w-11/12 rounded-md border-0 bg-blue-50 px-3 py-2 text-sm text-blue-700 dark:bg-neutral-700 dark:text-white`}
                            >
                                Выбрать связанные шоу
                            </button>
                        </div>
                        <div>
                            {selectedPersonAvatarImage && (
                                <img
                                    src={URL.createObjectURL(
                                        selectedPersonAvatarImage
                                    )}
                                    alt="Загруженное изображение"
                                    className="h-fit max-h-32 w-fit border bg-cover"
                                />
                            )}
                        </div>
                    </li>
                </ul>

                <div className="relative flex items-center justify-center">
                    {/* upload status indicator */}
                    <div className="mt-6 flex justify-center gap-4">
                        <div className={'absolute left-5'}>
                            {operationStatus && (
                                <p
                                    className={
                                        operationStatus.ok
                                            ? 'text-4xl text-green-500'
                                            : 'text-red-500'
                                    }
                                >
                                    {operationStatus?.message}
                                </p>
                            )}
                        </div>
                        {/* Submit Button */}
                        <button
                            type="submit"
                            onKeyDown={(event) =>
                                event.keyCode === 13 && event.preventDefault()
                            }
                            className="rounded-lg bg-blue-600 px-4 py-2 font-bold text-white transition-transform hover:bg-blue-700 focus:outline-none dark:bg-green-600 dark:hover:bg-green-700"
                            id="add-film-button"
                        >
                            Загрузить
                        </button>
                        {/* Clear Form */}
                        <div className="absolute right-0">
                            <button className="mt-3 p-0 text-xs text-gray-500 hover:text-gray-700 dark:text-blue-200 dark:hover:text-blue-300">
                                <u>Очистить форму</u>
                            </button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    )
}

export default FormAddCreator
