import Position from './Position'
import UserPic from './UserPic'

type ContentCreator = {
    id: string
    name: string
    surname: string
    nameLatin: string
    surnameLatin: string
    bornPlace: string
    heightCm: number
    personCategory: Position
    personCategory: Position
    userPic: UserPic
    isDead: boolean
    birthDate: string
    deathDate: string
}

export default ContentCreator
