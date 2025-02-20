import PersonCategory from "./PersonCategory";
import UserPic from "./UserPic";

type ContentCreator = {
  id: string;
  name: string;
  surname: string;
  nameLatin: string;
  surnameLatin: string;
  bornPlace: string;
  heightCm: number;
  age: number;
  personCategory: PersonCategory;
  userPic: UserPic;
  isDead: boolean;
  birthDate: string;
  deathDate: string;
};

export default ContentCreator;
