import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { getFirestore } from 'firebase/firestore';

const firebaseConfig = {
  apiKey: 'AIzaSyDylptgicTGHdPbfY9TCeYJA3O0xdZW9so',
  authDomain: 'mytodolist-2adca.firebaseapp.com',
  projectId: 'mytodolist-2adca',
  storageBucket: 'mytodolist-2adca.firebasestorage.app',
  messagingSenderId: '423335179907',
  appId: '1:423335179907:web:e87b202bf0e57640d68db6',
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const db = getFirestore(app);

export { app, auth, db };
