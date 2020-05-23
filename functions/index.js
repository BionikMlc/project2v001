const functions = require('firebase-functions');
const admin =  require('firebase-admin');

admin.initializeApp();

exports.addAdminRole = functions.https.onCall((data,context)=>{

    return admin.auth().getUserByEmail(data.email).then(user =>{
        return admin.auth().setCustomUserClaims(user.uid,{admin: true});
    }).then(()=>{
        return {message: `Success ${data.email} has been made an admin`}
    }).catch(err => {
        return err;
    });
});

exports.deleteUser = functions.https.onCall((data,context)=>{
    admin.auth().deleteUser(data.uid)
    .then(function() {
      console.log('Successfully deleted user'+data.uid);
    })
    .catch(function(error) {
      console.log('Error deleting user:', error,data.uid);
    });
});


