import ContentDetails from "./ContentDetails";
import UserPic from "./UserPic";

type ContentCreator = {
  fullname: string;
  fullnameEng: string;
  bornPlace: string;
  heightMeters: number;
  age: number;
  userPic?: UserPic;
  filmsParticipated?: ContentDetails[];
  birthDate: string; // Format: dd.MM.yyyy
  deathDate?: string | null; // Optional
  isDead: boolean;
};

export default ContentCreator;
